package it_service_desk_api.controllers;

import it_service_desk_api.dtos.users_dtos.requests.LoginRequest;
import it_service_desk_api.dtos.users_dtos.requests.NewUserRequest;
import it_service_desk_api.dtos.users_dtos.responses.AuthResponse;
import it_service_desk_api.dtos.users_dtos.responses.UserResponse;
import it_service_desk_api.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    } // end of constructor

    // This public endpoint creates a standard USER account with an encoded password.
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody NewUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    } // end of register method

    // This public endpoint validates credentials and returns a bearer token.
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    } // end of login method
} // end of class body
