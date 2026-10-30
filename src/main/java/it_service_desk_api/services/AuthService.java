package it_service_desk_api.services;

import it_service_desk_api.dtos.users_dtos.requests.LoginRequest;
import it_service_desk_api.dtos.users_dtos.requests.NewUserRequest;
import it_service_desk_api.dtos.users_dtos.responses.AuthResponse;
import it_service_desk_api.dtos.users_dtos.responses.UserResponse;
import it_service_desk_api.entities.Users;
import it_service_desk_api.entities.enums.Roles;
import it_service_desk_api.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Locale;

@Service
public class AuthService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final long tokenExpirationSeconds;

    public AuthService(
            UsersRepository usersRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtEncoder jwtEncoder,
            @Value("${api.security.token.expiration-seconds}") long tokenExpirationSeconds
    ) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.tokenExpirationSeconds = tokenExpirationSeconds;
    } // end of constructor

    // Public registration always assigns USER and persists only the BCrypt hash.
    public UserResponse register(NewUserRequest request) {
        String email = normalizeEmail(request.email());

        if (usersRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        Users user = new Users(
                request.name().trim(),
                email,
                passwordEncoder.encode(request.password()),
                Roles.USER
        );

        return UserResponse.response(usersRepository.save(user));
    } // end of register method

    // Login validates the credentials, then issues a signed token with the user's role.
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.password())
            );
        } catch (AuthenticationException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationServiceException("Authenticated user was not found"));

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(tokenExpirationSeconds);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("it_service_desk_api")
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .build();

        String accessToken = jwtEncoder.encode(
                JwtEncoderParameters.from(
                        JwsHeader.with(MacAlgorithm.HS256).build(),
                        claims
                )
        ).getTokenValue();

        return new AuthResponse(
                "Bearer",
                accessToken,
                tokenExpirationSeconds,
                UserResponse.response(user)
        );
    } // end of login method

    // Normalizing email addresses keeps registration and login lookups consistent.
    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    } // end of normalizeEmail method
} // end of class body
