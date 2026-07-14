package com.travelagency.userservice.service;

import com.travelagency.userservice.config.SecurityLockoutConfig;
import com.travelagency.userservice.dto.ChangePasswordRequest;
import com.travelagency.userservice.dto.LoginRequest;
import com.travelagency.userservice.dto.UserRegistrationRequest;
import com.travelagency.userservice.entity.User;
import com.travelagency.userservice.exception.BusinessRuleException;
import com.travelagency.userservice.exception.ResourceNotFoundException;
import com.travelagency.userservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

// Registro, login y gestion de usuarios (Epica 1). Autenticacion simple local (email +
// contrasena propia); se reemplazara por Keycloak mas adelante.
@Service
public class UserService {

    private static final String ROLE_ADMIN = "ADMIN";

    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern DIGIT = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR = Pattern.compile("[@#$%^&+=!*]");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityLockoutConfig securityLockoutConfig;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                        SecurityLockoutConfig securityLockoutConfig) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityLockoutConfig = securityLockoutConfig;
    }

    // REGISTRAR un nuevo usuario. La contrasena se guarda hasheada (BCrypt), nunca en texto plano.
    public User register(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("El email ya esta registrado: " + request.getEmail());
        }

        validatePasswordStrength(request.getPassword());

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setIdentityDocument(request.getIdentityDocument());
        user.setNationality(request.getNationality());
        user.setRole("CLIENT");
        user.setActive(true);

        return userRepository.save(user);
    }

    // Contrasena: minimo 8 caracteres, una mayuscula, una minuscula, un numero y un
    // caracter especial (@#$%^&+=!*). Indica explicitamente cual requisito falta.
    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new BusinessRuleException("La contrasena debe tener al menos 8 caracteres");
        }
        if (!UPPERCASE.matcher(password).find()) {
            throw new BusinessRuleException("La contrasena debe tener al menos una mayuscula");
        }
        if (!LOWERCASE.matcher(password).find()) {
            throw new BusinessRuleException("La contrasena debe tener al menos una minuscula");
        }
        if (!DIGIT.matcher(password).find()) {
            throw new BusinessRuleException("La contrasena debe tener al menos un numero");
        }
        if (!SPECIAL_CHAR.matcher(password).find()) {
            throw new BusinessRuleException("La contrasena debe tener al menos un caracter especial (@#$%^&+=!*)");
        }
    }

    // INICIAR SESION. Verifica bloqueo por intentos fallidos, luego contrasena, luego que
    // la cuenta este activa. El mensaje de "credenciales invalidas" es generico (no
    // distingue "email no existe" de "contrasena incorrecta") para no dar pistas a quien
    // intenta adivinar credenciales.
    //
    // noRollbackFor: por defecto @Transactional deshace todo cuando el metodo termina
    // lanzando una RuntimeException (como BusinessRuleException), lo que borraria
    // silenciosamente el incremento de failedLoginAttempts que handleFailedLogin() ya
    // guardo, dejando el bloqueo de cuenta inoperante.
    @Transactional(noRollbackFor = BusinessRuleException.class)
    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessRuleException("Email o contrasena invalidos"));

        if (isAccountLocked(user)) {
            throw new BusinessRuleException(
                    "La cuenta esta temporalmente bloqueada por exceso de intentos fallidos. Intenta mas tarde.");
        }

        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            handleFailedLogin(user);
            throw new BusinessRuleException("Email o contrasena invalidos");
        }

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new BusinessRuleException("La cuenta esta desactivada. Contacta a soporte.");
        }

        user.setFailedLoginAttempts(0);
        user.setLockUntil(null);
        return userRepository.save(user);
    }

    private boolean isAccountLocked(User user) {
        return user.getLockUntil() != null && user.getLockUntil().isAfter(LocalDateTime.now());
    }

    private void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        if (attempts >= securityLockoutConfig.getMaxFailedAttempts()) {
            user.setLockUntil(LocalDateTime.now().plusMinutes(securityLockoutConfig.getLockDurationMinutes()));
        }
        userRepository.save(user);
    }

    // CAMBIAR LA CONTRASENA. El dueno de la cuenta, o un ADMIN, pueden hacerlo. En la
    // practica solo el propio dueno puede tener exito, ya que se exige la contrasena actual.
    public User changePassword(Long requestingUserId, String requestingRole, Long targetUserId,
                                ChangePasswordRequest request) {
        requireOwnerOrAdmin(requestingUserId, requestingRole, targetUserId);

        User user = getUserById(targetUserId);
        if (user.getPassword() == null || !passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessRuleException("La contrasena actual es incorrecta");
        }

        validatePasswordStrength(request.getNewPassword());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        return userRepository.save(user);
    }

    // LISTAR todos los usuarios. Solo un ADMIN puede hacerlo.
    @Transactional(readOnly = true)
    public List<User> getAllUsers(String requestingRole) {
        requireAdmin(requestingRole);
        return userRepository.findAll();
    }

    // OBTENER un usuario por ID (uso interno, sin control de acceso).
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    // OBTENER un usuario por ID, para un endpoint publico: solo el dueno o un ADMIN.
    @Transactional(readOnly = true)
    public User getUserByIdForRequester(Long id, Long requestingUserId, String requestingRole) {
        requireOwnerOrAdmin(requestingUserId, requestingRole, id);
        return getUserById(id);
    }

    // ACTIVAR o DESACTIVAR una cuenta (borrado logico). Solo un ADMIN puede hacerlo.
    public User changeActiveStatus(String requestingRole, Long targetUserId, boolean active) {
        requireAdmin(requestingRole);
        User targetUser = getUserById(targetUserId);
        targetUser.setActive(active);
        return userRepository.save(targetUser);
    }

    private void requireAdmin(String requestingRole) {
        if (!ROLE_ADMIN.equals(requestingRole)) {
            throw new BusinessRuleException("Solo un administrador puede realizar esta accion");
        }
    }

    private void requireOwnerOrAdmin(Long requestingUserId, String requestingRole, Long resourceOwnerId) {
        boolean isOwner = requestingUserId != null && requestingUserId.equals(resourceOwnerId);
        boolean isAdmin = ROLE_ADMIN.equals(requestingRole);
        if (!isOwner && !isAdmin) {
            throw new BusinessRuleException("No tienes permiso para acceder a este recurso");
        }
    }
}
