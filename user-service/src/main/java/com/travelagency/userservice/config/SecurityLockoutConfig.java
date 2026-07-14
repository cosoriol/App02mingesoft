package com.travelagency.userservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// Bloqueo de cuenta por intentos fallidos (Epica 1, Regla 6): tras N intentos
// fallidos consecutivos, la cuenta queda bloqueada por M minutos.
// Valores configurables via app.security.max-failed-attempts / lock-duration-minutes.
@Component
@ConfigurationProperties(prefix = "app.security")
public class SecurityLockoutConfig {

    private int maxFailedAttempts = 5;
    private int lockDurationMinutes = 30;

    public int getMaxFailedAttempts() {
        return maxFailedAttempts;
    }

    public void setMaxFailedAttempts(int maxFailedAttempts) {
        this.maxFailedAttempts = maxFailedAttempts;
    }

    public int getLockDurationMinutes() {
        return lockDurationMinutes;
    }

    public void setLockDurationMinutes(int lockDurationMinutes) {
        this.lockDurationMinutes = lockDurationMinutes;
    }
}
