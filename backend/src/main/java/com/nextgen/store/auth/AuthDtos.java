package com.nextgen.store.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {
    private AuthDtos() {}
    public record RegisterRequest(
        @NotBlank @Size(max=80) String firstName,
        @Size(max=80) String lastName,
        @NotBlank @Email @Size(max=255) String email,
        @NotBlank @Size(min=8, max=72) String password
    ) {}
    public record LoginRequest(
        @NotBlank @Email @Size(max=255) String email,
        @NotBlank String password
    ) {}
    public record AuthResponse(String token, long expiresInMs, UserResponse user) {}
    public record UserResponse(Long id, String email, String firstName, String lastName, Role role) {
        static UserResponse from(User u){ return new UserResponse(u.getId(),u.getEmail(),u.getFirstName(),u.getLastName(),u.getRole()); }
    }
}
