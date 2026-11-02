package it_service_desk_api.dtos.users_dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewUserRequest (
        // These constraints reject missing or blank values before the registration logic runs.
        @NotBlank (message = "Please enter your name")
        String name,

        @NotBlank (message = "Please enter your email")
        @Email (message = "Please enter a valid email")
        String email,

        @NotBlank (message = "Please enter your password")
        @Size (min = 8, max = 72, message = "Password must contain between 8 and 72 characters")
        String password
) {}
