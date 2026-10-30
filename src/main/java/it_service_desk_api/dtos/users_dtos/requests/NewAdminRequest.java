package it_service_desk_api.dtos.users_dtos.requests;

import it_service_desk_api.entities.enums.Roles;
import jakarta.validation.constraints.NotNull;


public record NewAdminRequest (
        @NotNull (message = "Please enter your name")
        String name,

        @NotNull (message = "Please enter a valid email")
        String email,

        @NotNull (message = "Please enter your password")
        String password,

        @NotNull (message = "Please choose your role")
        Roles role
) {}
