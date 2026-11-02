package it_service_desk_api.services;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthRateLimiterTests {

    @Test
    void allowsLoginAttemptsUpToConfiguredLimitPerAddress() {
        AuthRateLimiter rateLimiter = new AuthRateLimiter(2, 60, 2, 3600);

        assertDoesNotThrow(() -> rateLimiter.checkLoginAttempt("192.0.2.1"));
        assertDoesNotThrow(() -> rateLimiter.checkLoginAttempt("192.0.2.1"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> rateLimiter.checkLoginAttempt("192.0.2.1")
        );
        assertEquals(429, exception.getStatusCode().value());
    }

    @Test
    void tracksAddressesAndRegistrationSeparately() {
        AuthRateLimiter rateLimiter = new AuthRateLimiter(1, 60, 1, 3600);

        assertDoesNotThrow(() -> rateLimiter.checkLoginAttempt("192.0.2.1"));
        assertDoesNotThrow(() -> rateLimiter.checkLoginAttempt("192.0.2.2"));
        assertDoesNotThrow(() -> rateLimiter.checkRegistrationAttempt("192.0.2.1"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> rateLimiter.checkRegistrationAttempt("192.0.2.1")
        );
        assertEquals(429, exception.getStatusCode().value());
    }
}
