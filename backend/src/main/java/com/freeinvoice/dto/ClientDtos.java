package com.freeinvoice.dto;

import com.freeinvoice.domain.Client;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class ClientDtos {

    public record ClientCreateRequest(
            @NotBlank String companyName,
            @NotBlank String contactName,
            @NotBlank @Email String email,
            String phone
    ) {}

    public record ClientUpdateRequest(
            String companyName,
            String contactName,
            String phone
    ) {}

    public record ClientResponse(
            Long id,
            String companyName,
            String contactName,
            String email,
            String phone,
            boolean isRegistered,
            LocalDateTime createdAt
    ) {
        public static ClientResponse from(Client c) {
            return new ClientResponse(
                    c.getId(), c.getCompanyName(), c.getContactName(), c.getEmail(),
                    c.getPhone(), c.isRegistered(), c.getCreatedAt()
            );
        }
    }
}
