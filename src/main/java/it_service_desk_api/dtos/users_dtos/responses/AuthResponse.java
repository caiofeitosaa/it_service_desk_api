package it_service_desk_api.dtos.users_dtos.responses;

// This response returns the bearer token and safe user data after a successful login.
public record AuthResponse (
        String tokenType,
        String accessToken,
        long expiresIn,
        UserResponse user
) {}
