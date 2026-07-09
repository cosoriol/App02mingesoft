package com.travelagency.confirmationservice.exception;

// Se lanza cuando una operacion viola una regla de negocio de confirmacion/seguimiento
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
