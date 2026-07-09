package com.travelagency.paymentservice.exception;

// Se lanza cuando una operacion viola una regla de negocio del proceso de pago
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
