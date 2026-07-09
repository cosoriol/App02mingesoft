package com.travelagency.bookingservice.exception;

// Se lanza cuando una operacion viola una regla de negocio del proceso de reserva
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
