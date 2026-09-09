package com.freeinvoice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quote_id", nullable = false, unique = true)
    private Quote quote;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "freelancer_id", nullable = false)
    private User freelancer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.UNPAID;

    private LocalDateTime paidAt;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    public Invoice(Quote quote, String invoiceNumber, LocalDate dueDate) {
        this.quote = quote;
        this.freelancer = quote.getFreelancer();
        this.client = quote.getClient();
        this.invoiceNumber = invoiceNumber;
        this.totalAmount = quote.getTotalAmount();
        this.dueDate = dueDate;
        this.issuedAt = LocalDateTime.now();
    }

    public InvoiceStatus resolvedStatus() {
        if (status == InvoiceStatus.UNPAID && dueDate.isBefore(LocalDate.now())) {
            return InvoiceStatus.OVERDUE;
        }
        return status;
    }

    public void markPaid() {
        this.status = InvoiceStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }
}
