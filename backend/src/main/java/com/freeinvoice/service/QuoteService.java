package com.freeinvoice.service;

import com.freeinvoice.domain.*;
import com.freeinvoice.dto.QuoteDtos.*;
import com.freeinvoice.exception.ApiException;
import com.freeinvoice.repository.ClientRepository;
import com.freeinvoice.repository.QuoteRepository;
import com.freeinvoice.repository.UserRepository;
import com.freeinvoice.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    public QuoteService(QuoteRepository quoteRepository, ClientRepository clientRepository,
                         UserRepository userRepository) {
        this.quoteRepository = quoteRepository;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
    }

    private User currentUser(UserPrincipal principal) {
        return userRepository.findById(principal.userId())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));
    }

    public List<QuoteSummaryResponse> list(UserPrincipal principal, QuoteStatus statusFilter) {
        User user = currentUser(principal);
        List<Quote> quotes;
        if (user.getRole() == Role.FREELANCER) {
            quotes = statusFilter != null
                    ? quoteRepository.findByFreelancerAndStatusOrderByCreatedAtDesc(user, statusFilter)
                    : quoteRepository.findByFreelancerOrderByCreatedAtDesc(user);
        } else {
            List<Client> myClientRecords = clientRepository.findByEmail(user.getEmail());
            if (myClientRecords.isEmpty()) {
                return List.of();
            }
            quotes = statusFilter != null
                    ? quoteRepository.findByClientInAndStatusOrderByCreatedAtDesc(myClientRecords, statusFilter)
                    : quoteRepository.findByClientInAndStatusNotOrderByCreatedAtDesc(myClientRecords, QuoteStatus.DRAFT);
        }
        return quotes.stream().map(QuoteSummaryResponse::from).toList();
    }

    Quote getAccessibleQuote(User user, Long quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "견적서를 찾을 수 없습니다."));
        if (user.getRole() == Role.FREELANCER) {
            if (!quote.getFreelancer().getId().equals(user.getId())) {
                throw new ApiException(HttpStatus.FORBIDDEN, "본인의 견적서가 아닙니다.");
            }
        } else {
            boolean owns = quote.getClient().getEmail().equals(user.getEmail());
            if (!owns || quote.getStatus() == QuoteStatus.DRAFT) {
                throw new ApiException(HttpStatus.FORBIDDEN, "열람 권한이 없습니다.");
            }
        }
        return quote;
    }

    public QuoteResponse getOne(UserPrincipal principal, Long id) {
        return QuoteResponse.from(getAccessibleQuote(currentUser(principal), id));
    }

    @Transactional
    public QuoteResponse create(UserPrincipal principal, QuoteCreateRequest request) {
        User freelancer = currentUser(principal);
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "거래처를 찾을 수 없습니다."));
        if (!client.getFreelancer().getId().equals(freelancer.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "본인의 거래처가 아닙니다.");
        }

        Quote quote = new Quote(freelancer, client, request.title(), request.memo(), request.validUntil());
        quoteRepository.save(quote);
        quote.replaceItems(buildItems(quote, request.items()));
        return QuoteResponse.from(quote);
    }

    private List<QuoteItem> buildItems(Quote quote, List<QuoteItemRequest> items) {
        List<QuoteItem> result = new ArrayList<>();
        int order = 1;
        for (QuoteItemRequest item : items) {
            result.add(new QuoteItem(quote, order++, item.name(), item.quantity(), item.unitPrice()));
        }
        return result;
    }

    @Transactional
    public QuoteResponse update(UserPrincipal principal, Long id, QuoteCreateRequest request) {
        User freelancer = currentUser(principal);
        Quote quote = getAccessibleQuote(freelancer, id);
        if (quote.getStatus() != QuoteStatus.DRAFT) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "임시저장(DRAFT) 상태의 견적서만 수정할 수 있습니다.");
        }
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "거래처를 찾을 수 없습니다."));
        quote.setClient(client);
        quote.setTitle(request.title());
        quote.setMemo(request.memo());
        quote.setValidUntil(request.validUntil());
        quote.replaceItems(buildItems(quote, request.items()));
        return QuoteResponse.from(quote);
    }

    @Transactional
    public void delete(UserPrincipal principal, Long id) {
        Quote quote = getAccessibleQuote(currentUser(principal), id);
        if (quote.getStatus() != QuoteStatus.DRAFT) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "임시저장(DRAFT) 상태의 견적서만 삭제할 수 있습니다.");
        }
        quoteRepository.delete(quote);
    }

    @Transactional
    public QuoteResponse send(UserPrincipal principal, Long id) {
        Quote quote = getAccessibleQuote(currentUser(principal), id);
        if (quote.getStatus() != QuoteStatus.DRAFT) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "임시저장(DRAFT) 상태의 견적서만 발송할 수 있습니다.");
        }
        quote.send();
        return QuoteResponse.from(quote);
    }

    @Transactional
    public QuoteResponse decide(UserPrincipal principal, Long id, DecisionRequest request) {
        User client = currentUser(principal);
        if (client.getRole() != Role.CLIENT) {
            throw new ApiException(HttpStatus.FORBIDDEN, "클라이언트만 승인/거절할 수 있습니다.");
        }
        Quote quote = getAccessibleQuote(client, id);
        if (quote.getStatus() != QuoteStatus.SENT) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "발송(SENT) 상태의 견적서만 승인/거절할 수 있습니다.");
        }
        if (request.decision() != QuoteStatus.APPROVED && request.decision() != QuoteStatus.REJECTED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "승인 또는 거절만 선택할 수 있습니다.");
        }
        quote.decide(request.decision());
        return QuoteResponse.from(quote);
    }
}
