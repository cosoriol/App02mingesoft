package com.travelagency.confirmationservice.exception;

// Se lanza cuando un cliente intenta acceder u operar sobre una reserva que no le pertenece,
// o cuando se requiere rol ADMIN y no se cumple
public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
