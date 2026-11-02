package it_service_desk_api.dtos.users_dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest (
        // Input validation provides clear errors before the credentials reach Spring Security.
        @NotBlank (message = "Please enter your email")
        @Email (message = "Please enter a valid email")
        String email,

        @NotBlank (message = "Please enter your password")
        @Size (max = 72, message = "Password must contain at most 72 characters")
        String password
) {}
