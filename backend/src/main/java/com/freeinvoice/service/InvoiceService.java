package com.freeinvoice.service;

import com.freeinvoice.domain.*;
import com.freeinvoice.dto.InvoiceDtos.InvoiceCreateRequest;
import com.freeinvoice.dto.InvoiceDtos.InvoiceResponse;
import com.freeinvoice.dto.InvoiceDtos.InvoiceStatusRequest;
import com.freeinvoice.exception.ApiException;
import com.freeinvoice.repository.ClientRepository;
import com.freeinvoice.repository.InvoiceRepository;
import com.freeinvoice.repository.UserRepository;
import com.freeinvoice.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final QuoteService quoteService;

    public InvoiceService(InvoiceRepository invoiceRepository, ClientRepository clientRepository,
                           UserRepository userRepository, QuoteService quoteService) {
        this.invoiceRepository = invoiceRepository;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.quoteService = quoteService;
    }

    private User currentUser(UserPrincipal principal) {
        return userRepository.findById(principal.userId())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public InvoiceResponse create(UserPrincipal principal, Long quoteId, InvoiceCreateRequest request) {
        User freelancer = currentUser(principal);
        Quote quote = quoteService.getAccessibleQuote(freelancer, quoteId);
        if (quote.getStatus() != QuoteStatus.APPROVED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "승인(APPROVED)된 견적서만 인보이스를 발행할 수 있습니다.");
        }
        if (invoiceRepository.findByQuote(quote).isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "이미 해당 견적서로 발행된 인보이스가 있습니다.");
        }
        String invoiceNumber = String.format("INV-%d-%04d", Year.now().getValue(), invoiceRepository.count() + 1);
        Invoice invoice = new Invoice(quote, invoiceNumber, request.dueDate());
        invoiceRepository.save(invoice);
        return InvoiceResponse.from(invoice);
    }

    public List<InvoiceResponse> list(UserPrincipal principal, InvoiceStatus statusFilter) {
        User user = currentUser(principal);
        List<Invoice> invoices;
        if (user.getRole() == Role.FREELANCER) {
            invoices = invoiceRepository.findByFreelancerOrderByIssuedAtDesc(user);
        } else {
            List<Client> myClientRecords = clientRepository.findByEmail(user.getEmail());
            invoices = myClientRecords.isEmpty() ? List.of() : invoiceRepository.findByClientInOrderByIssuedAtDesc(myClientRecords);
        }
        return invoices.stream()
                .map(InvoiceResponse::from)
                .filter(r -> statusFilter == null || r.status() == statusFilter)
                .toList();
    }

    Invoice getAccessible(User user, Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "인보이스를 찾을 수 없습니다."));
        boolean owns = user.getRole() == Role.FREELANCER
                ? invoice.getFreelancer().getId().equals(user.getId())
                : invoice.getClient().getEmail().equals(user.getEmail());
        if (!owns) {
            throw new ApiException(HttpStatus.FORBIDDEN, "열람 권한이 없습니다.");
        }
        return invoice;
    }

    public InvoiceResponse getOne(UserPrincipal principal, Long id) {
        return InvoiceResponse.from(getAccessible(currentUser(principal), id));
    }

    @Transactional
    public InvoiceResponse updateStatus(UserPrincipal principal, Long id, InvoiceStatusRequest request) {
        User freelancer = currentUser(principal);
        if (freelancer.getRole() != Role.FREELANCER) {
            throw new ApiException(HttpStatus.FORBIDDEN, "프리랜서만 입금 확인 처리를 할 수 있습니다.");
        }
        Invoice invoice = getAccessible(freelancer, id);
        if (request.status() != InvoiceStatus.PAID) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "결제완료(PAID) 상태로만 변경할 수 있습니다.");
        }
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "이미 결제완료된 인보이스입니다.");
        }
        invoice.markPaid();
        return InvoiceResponse.from(invoice);
    }
}
