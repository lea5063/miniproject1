package com.freeinvoice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String companyName;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public User(String email, String password, String name, Role role, String companyName) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.companyName = companyName;
        this.createdAt = LocalDateTime.now();
    }
}
