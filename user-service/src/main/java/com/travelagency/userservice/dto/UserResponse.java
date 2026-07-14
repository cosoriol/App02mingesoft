package com.travelagency.userservice.dto;

import com.travelagency.userservice.entity.User;

import java.time.LocalDateTime;

// Datos de salida expuestos por la API para un usuario. Nunca incluye la contrasena.
public class UserResponse {

    private final Long id;
    private final String fullName;
    private final String email;
    private final String phone;
    private final String identityDocument;
    private final String nationality;
    private final String role;
    private final Boolean active;
    private final Integer failedLoginAttempts;
    private final LocalDateTime lockUntil;
    private final LocalDateTime createdAt;

    public UserResponse(Long id, String fullName, String email, String phone, String identityDocument,
                         String nationality, String role, Boolean active, Integer failedLoginAttempts,
                         LocalDateTime lockUntil, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.identityDocument = identityDocument;
        this.nationality = nationality;
        this.role = role;
        this.active = active;
        this.failedLoginAttempts = failedLoginAttempts;
        this.lockUntil = lockUntil;
        this.createdAt = createdAt;
    }

    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getIdentityDocument(),
                user.getNationality(),
                user.getRole(),
                user.getActive(),
                user.getFailedLoginAttempts(),
                user.getLockUntil(),
                user.getCreatedAt());
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getIdentityDocument() {
        return identityDocument;
    }

    public String getNationality() {
        return nationality;
    }

    public String getRole() {
        return role;
    }

    public Boolean getActive() {
        return active;
    }

    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public LocalDateTime getLockUntil() {
        return lockUntil;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
