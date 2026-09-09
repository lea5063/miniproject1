package com.freeinvoice.repository;

import com.freeinvoice.domain.Client;
import com.freeinvoice.domain.Invoice;
import com.freeinvoice.domain.Quote;
import com.freeinvoice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByFreelancerOrderByIssuedAtDesc(User freelancer);
    List<Invoice> findByClientInOrderByIssuedAtDesc(List<Client> clients);
    Optional<Invoice> findByQuote(Quote quote);
    long countByFreelancer(User freelancer);
}
