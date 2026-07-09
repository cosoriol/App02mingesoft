package com.travelagency.bookingservice.exception;

// Se lanza cuando un usuario intenta acceder a una reserva que no le pertenece,
// o intenta una operacion reservada para administradores
public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
