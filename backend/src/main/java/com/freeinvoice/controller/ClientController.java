package com.freeinvoice.controller;

import com.freeinvoice.dto.ClientDtos.ClientCreateRequest;
import com.freeinvoice.dto.ClientDtos.ClientResponse;
import com.freeinvoice.dto.ClientDtos.ClientUpdateRequest;
import com.freeinvoice.security.UserPrincipal;
import com.freeinvoice.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public List<ClientResponse> list(@AuthenticationPrincipal UserPrincipal principal) {
        return clientService.list(principal);
    }

    @PostMapping
    public ResponseEntity<ClientResponse> create(@AuthenticationPrincipal UserPrincipal principal,
                                                  @Valid @RequestBody ClientCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.create(principal, request));
    }

    @PutMapping("/{id}")
    public ClientResponse update(@AuthenticationPrincipal UserPrincipal principal,
                                  @PathVariable Long id,
                                  @RequestBody ClientUpdateRequest request) {
        return clientService.update(principal, id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        clientService.delete(principal, id);
        return ResponseEntity.noContent().build();
    }
}
