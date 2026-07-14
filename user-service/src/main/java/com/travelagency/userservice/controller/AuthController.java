package com.travelagency.userservice.controller;

import com.travelagency.userservice.dto.LoginRequest;
import com.travelagency.userservice.dto.LoginResponse;
import com.travelagency.userservice.dto.UserRegistrationRequest;
import com.travelagency.userservice.dto.UserResponse;
import com.travelagency.userservice.entity.User;
import com.travelagency.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

// Endpoints de registro e inicio de sesion (Epica 1)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody UserRegistrationRequest request) {
        User created = userService.register(request);
        return UserResponse.fromEntity(created);
    }

    // "token" es por ahora un UUID simple (sin significado criptografico); llegara con Keycloak.
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        User user = userService.login(request);
        String token = UUID.randomUUID().toString();
        return new LoginResponse(UserResponse.fromEntity(user), token, "Login exitoso");
    }
}
