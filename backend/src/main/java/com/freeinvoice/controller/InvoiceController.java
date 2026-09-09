package com.freeinvoice.controller;

import com.freeinvoice.domain.InvoiceStatus;
import com.freeinvoice.dto.InvoiceDtos.InvoiceCreateRequest;
import com.freeinvoice.dto.InvoiceDtos.InvoiceResponse;
import com.freeinvoice.dto.InvoiceDtos.InvoiceStatusRequest;
import com.freeinvoice.security.UserPrincipal;
import com.freeinvoice.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping("/quotes/{id}/invoice")
    public ResponseEntity<InvoiceResponse> create(@AuthenticationPrincipal UserPrincipal principal,
                                                   @PathVariable Long id,
                                                   @Valid @RequestBody InvoiceCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.create(principal, id, request));
    }

    @GetMapping("/invoices")
    public List<InvoiceResponse> list(@AuthenticationPrincipal UserPrincipal principal,
                                       @RequestParam(required = false) InvoiceStatus status) {
        return invoiceService.list(principal, status);
    }

    @GetMapping("/invoices/{id}")
    public InvoiceResponse getOne(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return invoiceService.getOne(principal, id);
    }

    @PutMapping("/invoices/{id}/status")
    public InvoiceResponse updateStatus(@AuthenticationPrincipal UserPrincipal principal,
                                         @PathVariable Long id,
                                         @Valid @RequestBody InvoiceStatusRequest request) {
        return invoiceService.updateStatus(principal, id, request);
    }
}
