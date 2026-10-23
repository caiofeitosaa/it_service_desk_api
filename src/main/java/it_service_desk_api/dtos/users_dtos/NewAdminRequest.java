package it_service_desk_api.dtos.users_dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import javax.management.relation.Role;

public record NewAdminRequest (
        @NotNull
        String name,

        @NotNull
        String email,

        @NotNull
        String password,

        @NotNull
        Role role
) {}
