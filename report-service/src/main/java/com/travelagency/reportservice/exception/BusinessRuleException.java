package com.travelagency.reportservice.exception;

// Se lanza cuando una operacion viola una regla de negocio de reportes (incluye el chequeo de rol ADMIN)
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
