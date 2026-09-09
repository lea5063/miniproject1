package com.freeinvoice.security;

import com.freeinvoice.domain.Role;

public record UserPrincipal(Long userId, String email, Role role) {
}
