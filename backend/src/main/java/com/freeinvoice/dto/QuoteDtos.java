package com.freeinvoice.dto;

import com.freeinvoice.domain.Quote;
import com.freeinvoice.domain.QuoteItem;
import com.freeinvoice.domain.QuoteStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class QuoteDtos {

    public record QuoteItemRequest(
            @NotBlank String name,
            @NotNull @Min(1) Integer quantity,
            @NotNull @DecimalMin("0") BigDecimal unitPrice
    ) {}

    public record QuoteItemResponse(
            Long id,
            Integer itemOrder,
            String name,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal amount
    ) {
        public static QuoteItemResponse from(QuoteItem i) {
            return new QuoteItemResponse(i.getId(), i.getItemOrder(), i.getName(), i.getQuantity(), i.getUnitPrice(), i.getAmount());
        }
    }

    public record QuoteCreateRequest(
            @NotNull Long clientId,
            @NotBlank String title,
            String memo,
            LocalDate validUntil,
            @NotEmpty @Valid List<QuoteItemRequest> items
    ) {}

    public record DecisionRequest(@NotNull QuoteStatus decision) {}

    public record QuoteSummaryResponse(
            Long id,
            String title,
            String clientCompanyName,
            QuoteStatus status,
            BigDecimal totalAmount,
            LocalDate validUntil,
            LocalDateTime createdAt
    ) {
        public static QuoteSummaryResponse from(Quote q) {
            return new QuoteSummaryResponse(
                    q.getId(), q.getTitle(), q.getClient().getCompanyName(), q.getStatus(),
                    q.getTotalAmount(), q.getValidUntil(), q.getCreatedAt()
            );
        }
    }

    public record QuoteResponse(
            Long id,
            Long freelancerId,
            Long clientId,
            String title,
            String clientCompanyName,
            String memo,
            QuoteStatus status,
            BigDecimal totalAmount,
            LocalDate validUntil,
            List<QuoteItemResponse> items,
            LocalDateTime sentAt,
            LocalDateTime decidedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public static QuoteResponse from(Quote q) {
            return new QuoteResponse(
                    q.getId(), q.getFreelancer().getId(), q.getClient().getId(), q.getTitle(),
                    q.getClient().getCompanyName(), q.getMemo(), q.getStatus(), q.getTotalAmount(),
                    q.getValidUntil(),
                    q.getItems().stream().map(QuoteItemResponse::from).toList(),
                    q.getSentAt(), q.getDecidedAt(), q.getCreatedAt(), q.getUpdatedAt()
            );
        }
    }
}
