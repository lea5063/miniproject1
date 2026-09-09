package com.freeinvoice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "clients", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"freelancer_id", "email"})
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "freelancer_id", nullable = false)
    private User freelancer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_user_id")
    private User clientUser;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String contactName;

    @Column(nullable = false)
    private String email;

    private String phone;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Client(User freelancer, String companyName, String contactName, String email, String phone) {
        this.freelancer = freelancer;
        this.companyName = companyName;
        this.contactName = contactName;
        this.email = email;
        this.phone = phone;
        this.createdAt = LocalDateTime.now();
    }

    public boolean isRegistered() {
        return clientUser != null;
    }
}
