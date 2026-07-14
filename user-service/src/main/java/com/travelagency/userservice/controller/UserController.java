package com.travelagency.userservice.controller;

import com.travelagency.userservice.dto.ChangePasswordRequest;
import com.travelagency.userservice.dto.UserResponse;
import com.travelagency.userservice.entity.User;
import com.travelagency.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Gestion de usuarios (Epica 1): consulta, activacion/desactivacion, cambio de contrasena.
// requesterId/role viajan como parametros (igual que en el resto de los microservicios:
// no hay autenticacion real via token todavia, ver AuthController).
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Solo ADMIN
    @GetMapping
    public List<UserResponse> getAllUsers(@RequestParam String role) {
        return userService.getAllUsers(role).stream().map(UserResponse::fromEntity).toList();
    }

    // Dueno del perfil o ADMIN
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id, @RequestParam Long requesterId,
                                     @RequestParam String role) {
        User user = userService.getUserByIdForRequester(id, requesterId, role);
        return UserResponse.fromEntity(user);
    }

    // Solo ADMIN: activar o desactivar una cuenta (borrado logico)
    @PatchMapping("/{id}/active")
    public UserResponse changeActiveStatus(@PathVariable Long id, @RequestParam boolean active,
                                            @RequestParam String role) {
        User updated = userService.changeActiveStatus(role, id, active);
        return UserResponse.fromEntity(updated);
    }

    // Dueno del perfil o ADMIN (en la practica solo el dueno, ya que exige la contrasena actual)
    @PostMapping("/{id}/change-password")
    public UserResponse changePassword(@PathVariable Long id, @RequestParam Long requesterId,
                                        @RequestParam String role,
                                        @Valid @RequestBody ChangePasswordRequest request) {
        User updated = userService.changePassword(requesterId, role, id, request);
        return UserResponse.fromEntity(updated);
    }
}
