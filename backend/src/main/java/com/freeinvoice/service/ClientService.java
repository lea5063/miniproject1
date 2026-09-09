package com.freeinvoice.service;

import com.freeinvoice.domain.Client;
import com.freeinvoice.domain.User;
import com.freeinvoice.dto.ClientDtos.ClientCreateRequest;
import com.freeinvoice.dto.ClientDtos.ClientResponse;
import com.freeinvoice.dto.ClientDtos.ClientUpdateRequest;
import com.freeinvoice.exception.ApiException;
import com.freeinvoice.repository.ClientRepository;
import com.freeinvoice.repository.InvoiceRepository;
import com.freeinvoice.repository.QuoteRepository;
import com.freeinvoice.repository.UserRepository;
import com.freeinvoice.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final QuoteRepository quoteRepository;
    private final InvoiceRepository invoiceRepository;

    public ClientService(ClientRepository clientRepository, UserRepository userRepository,
                          QuoteRepository quoteRepository, InvoiceRepository invoiceRepository) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.quoteRepository = quoteRepository;
        this.invoiceRepository = invoiceRepository;
    }

    private User currentFreelancer(UserPrincipal principal) {
        return userRepository.findById(principal.userId())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));
    }

    public List<ClientResponse> list(UserPrincipal principal) {
        User freelancer = currentFreelancer(principal);
        return clientRepository.findByFreelancerOrderByCreatedAtDesc(freelancer).stream()
                .map(ClientResponse::from)
                .toList();
    }

    @Transactional
    public ClientResponse create(UserPrincipal principal, ClientCreateRequest request) {
        User freelancer = currentFreelancer(principal);
        if (clientRepository.existsByFreelancerAndEmail(freelancer, request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "이미 등록된 이메일의 거래처입니다.");
        }
        Client client = new Client(freelancer, request.companyName(), request.contactName(),
                request.email(), request.phone());

        // 해당 이메일로 이미 CLIENT 계정이 가입되어 있으면 즉시 연결
        userRepository.findByEmail(request.email())
                .filter(u -> u.getRole() == com.freeinvoice.domain.Role.CLIENT)
                .ifPresent(client::setClientUser);

        clientRepository.save(client);
        return ClientResponse.from(client);
    }

    Client getOwnedClient(User freelancer, Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "거래처를 찾을 수 없습니다."));
        if (!client.getFreelancer().getId().equals(freelancer.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "본인의 거래처가 아닙니다.");
        }
        return client;
    }

    @Transactional
    public ClientResponse update(UserPrincipal principal, Long id, ClientUpdateRequest request) {
        Client client = getOwnedClient(currentFreelancer(principal), id);
        if (request.companyName() != null) client.setCompanyName(request.companyName());
        if (request.contactName() != null) client.setContactName(request.contactName());
        if (request.phone() != null) client.setPhone(request.phone());
        return ClientResponse.from(client);
    }

    @Transactional
    public void delete(UserPrincipal principal, Long id) {
        User freelancer = currentFreelancer(principal);
        Client client = getOwnedClient(freelancer, id);
        boolean hasQuotes = !quoteRepository.findByFreelancerOrderByCreatedAtDesc(freelancer).stream()
                .filter(q -> q.getClient().getId().equals(id))
                .toList().isEmpty();
        if (hasQuotes) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "연결된 견적서/인보이스가 존재하여 삭제할 수 없습니다.");
        }
        clientRepository.delete(client);
    }
}
