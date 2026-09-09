package com.freeinvoice.dto;

import com.freeinvoice.domain.Role;
import com.freeinvoice.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class AuthDtos {

    public record SignupRequest(
            @NotBlank @Email String email,
            @NotBlank String password,
            @NotBlank String name,
            @NotNull Role role,
            String companyName
    ) {}

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}

    public record UserResponse(
            Long id,
            String email,
            String name,
            Role role,
            String companyName,
            LocalDateTime createdAt
    ) {
        public static UserResponse from(User u) {
            return new UserResponse(u.getId(), u.getEmail(), u.getName(), u.getRole(), u.getCompanyName(), u.getCreatedAt());
        }
    }

    public record LoginResponse(String token, UserResponse user) {}
}
