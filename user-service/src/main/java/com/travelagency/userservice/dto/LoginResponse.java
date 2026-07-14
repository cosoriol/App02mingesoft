package com.travelagency.userservice.dto;

// Respuesta de un login exitoso. "token" es por ahora un UUID simple, sin significado
// criptografico (no es un JWT real); llegara con Keycloak mas adelante.
public class LoginResponse {

    private final UserResponse user;
    private final String token;
    private final String message;

    public LoginResponse(UserResponse user, String token, String message) {
        this.user = user;
        this.token = token;
        this.message = message;
    }

    public UserResponse getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }
}
