package com.freeinvoice.controller;

import com.freeinvoice.domain.QuoteStatus;
import com.freeinvoice.dto.QuoteDtos.*;
import com.freeinvoice.security.UserPrincipal;
import com.freeinvoice.service.QuoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quotes")
public class QuoteController {

    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping
    public List<QuoteSummaryResponse> list(@AuthenticationPrincipal UserPrincipal principal,
                                            @RequestParam(required = false) QuoteStatus status) {
        return quoteService.list(principal, status);
    }

    @PostMapping
    public ResponseEntity<QuoteResponse> create(@AuthenticationPrincipal UserPrincipal principal,
                                                 @Valid @RequestBody QuoteCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quoteService.create(principal, request));
    }

    @GetMapping("/{id}")
    public QuoteResponse getOne(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return quoteService.getOne(principal, id);
    }

    @PutMapping("/{id}")
    public QuoteResponse update(@AuthenticationPrincipal UserPrincipal principal,
                                 @PathVariable Long id,
                                 @Valid @RequestBody QuoteCreateRequest request) {
        return quoteService.update(principal, id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        quoteService.delete(principal, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/send")
    public QuoteResponse send(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return quoteService.send(principal, id);
    }

    @PutMapping("/{id}/decision")
    public QuoteResponse decide(@AuthenticationPrincipal UserPrincipal principal,
                                 @PathVariable Long id,
                                 @Valid @RequestBody DecisionRequest request) {
        return quoteService.decide(principal, id, request);
    }
}
