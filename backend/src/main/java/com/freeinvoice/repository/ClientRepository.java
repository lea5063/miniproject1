package com.freeinvoice.repository;

import com.freeinvoice.domain.Client;
import com.freeinvoice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByFreelancerOrderByCreatedAtDesc(User freelancer);
    Optional<Client> findByFreelancerAndEmail(User freelancer, String email);
    List<Client> findByEmail(String email);
    boolean existsByFreelancerAndEmail(User freelancer, String email);
}
