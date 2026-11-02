package it_service_desk_api.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AuthRateLimiter {

    private static final long MAX_TRACKED_ADDRESSES = 100_000;

    // Os contadores são locais à instância e expiram automaticamente ao fim de cada janela.
    private final Cache<String, AtomicInteger> loginAttempts;
    private final Cache<String, AtomicInteger> registrationAttempts;
    private final int loginMaxAttempts;
    private final int registrationMaxAttempts;

    public AuthRateLimiter(
            @Value("${api.security.login.max-attempts:10}") int loginMaxAttempts,
            @Value("${api.security.login.window-seconds:60}") long loginWindowSeconds,
            @Value("${api.security.registration.max-attempts:5}") int registrationMaxAttempts,
            @Value("${api.security.registration.window-seconds:3600}") long registrationWindowSeconds
    ) {
        if (loginMaxAttempts < 1 || loginWindowSeconds < 1
                || registrationMaxAttempts < 1 || registrationWindowSeconds < 1) {
            throw new IllegalStateException("Authentication rate limits must be positive");
        }

        this.loginMaxAttempts = loginMaxAttempts;
        this.registrationMaxAttempts = registrationMaxAttempts;
        this.loginAttempts = createWindowCache(loginWindowSeconds);
        this.registrationAttempts = createWindowCache(registrationWindowSeconds);
    }

    // Restringe tentativas de login por endereço de origem, sem bloquear a conta do usuário.
    public void checkLoginAttempt(String remoteAddress) {
        checkAttempt(loginAttempts, remoteAddress, loginMaxAttempts);
    }

    // Evita criação automatizada de muitas contas a partir do mesmo endereço.
    public void checkRegistrationAttempt(String remoteAddress) {
        checkAttempt(registrationAttempts, remoteAddress, registrationMaxAttempts);
    }

    private Cache<String, AtomicInteger> createWindowCache(long windowSeconds) {
        return Caffeine.newBuilder()
                .maximumSize(MAX_TRACKED_ADDRESSES)
                .expireAfterWrite(Duration.ofSeconds(windowSeconds))
                .build();
    }

    private void checkAttempt(Cache<String, AtomicInteger> attempts, String remoteAddress, int maxAttempts) {
        String addressKey = remoteAddress == null || remoteAddress.isBlank() ? "unknown" : remoteAddress;
        int currentAttempts = attempts.asMap()
                .computeIfAbsent(addressKey, ignored -> new AtomicInteger())
                .incrementAndGet();

        if (currentAttempts > maxAttempts) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
        }
    }
}
