package com.freeinvoice.repository;

import com.freeinvoice.domain.Client;
import com.freeinvoice.domain.Quote;
import com.freeinvoice.domain.QuoteStatus;
import com.freeinvoice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuoteRepository extends JpaRepository<Quote, Long> {
    List<Quote> findByFreelancerOrderByCreatedAtDesc(User freelancer);
    List<Quote> findByFreelancerAndStatusOrderByCreatedAtDesc(User freelancer, QuoteStatus status);
    List<Quote> findByClientInAndStatusNotOrderByCreatedAtDesc(List<Client> clients, QuoteStatus excludedStatus);
    List<Quote> findByClientInAndStatusOrderByCreatedAtDesc(List<Client> clients, QuoteStatus status);
}
