package com.freeinvoice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "quote_items")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuoteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quote_id", nullable = false)
    private Quote quote;

    @Column(nullable = false)
    private Integer itemOrder;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    public QuoteItem(Quote quote, int itemOrder, String name, int quantity, BigDecimal unitPrice) {
        this.quote = quote;
        this.itemOrder = itemOrder;
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
