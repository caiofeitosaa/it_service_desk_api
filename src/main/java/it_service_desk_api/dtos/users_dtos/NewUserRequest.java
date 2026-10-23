package it_service_desk_api.dtos.users_dtos;

import jakarta.validation.constraints.NotNull;

public record NewUserRequest (
        @NotNull
        String name,

        @NotNull
        String email,

        @NotNull
        String password
) {}
