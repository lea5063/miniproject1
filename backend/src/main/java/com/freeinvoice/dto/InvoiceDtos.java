package com.freeinvoice.dto;

import com.freeinvoice.domain.Invoice;
import com.freeinvoice.domain.InvoiceStatus;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class InvoiceDtos {

    public record InvoiceCreateRequest(@NotNull LocalDate dueDate) {}

    public record InvoiceStatusRequest(@NotNull InvoiceStatus status) {}

    public record InvoiceResponse(
            Long id,
            String invoiceNumber,
            Long quoteId,
            String quoteTitle,
            String clientCompanyName,
            BigDecimal totalAmount,
            LocalDate dueDate,
            InvoiceStatus status,
            LocalDateTime paidAt,
            LocalDateTime issuedAt
    ) {
        public static InvoiceResponse from(Invoice inv) {
            return new InvoiceResponse(
                    inv.getId(), inv.getInvoiceNumber(), inv.getQuote().getId(), inv.getQuote().getTitle(),
                    inv.getClient().getCompanyName(), inv.getTotalAmount(), inv.getDueDate(),
                    inv.resolvedStatus(), inv.getPaidAt(), inv.getIssuedAt()
            );
        }
    }
}
