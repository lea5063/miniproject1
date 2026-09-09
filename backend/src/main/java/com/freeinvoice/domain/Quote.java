package com.freeinvoice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quotes")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "freelancer_id", nullable = false)
    private User freelancer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuoteStatus status = QuoteStatus.DRAFT;

    private LocalDate validUntil;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    private LocalDateTime sentAt;
    private LocalDateTime decidedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("itemOrder ASC")
    private List<QuoteItem> items = new ArrayList<>();

    public Quote(User freelancer, Client client, String title, String memo, LocalDate validUntil) {
        this.freelancer = freelancer;
        this.client = client;
        this.title = title;
        this.memo = memo;
        this.validUntil = validUntil;
        this.createdAt = LocalDateTime.now();
    }

    public void replaceItems(List<QuoteItem> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
        recalculateTotal();
    }

    public void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(QuoteItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.updatedAt = LocalDateTime.now();
    }

    public void send() {
        this.status = QuoteStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    public void decide(QuoteStatus decision) {
        this.status = decision;
        this.decidedAt = LocalDateTime.now();
    }
}
