package com.travelagency.packageservice.exception;

// Se lanza cuando una operacion viola una regla de negocio
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
