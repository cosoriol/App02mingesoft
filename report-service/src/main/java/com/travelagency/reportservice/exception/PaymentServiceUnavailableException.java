package com.travelagency.reportservice.exception;

// Se lanza cuando payment-service no esta registrado en Eureka o no responde
public class PaymentServiceUnavailableException extends RuntimeException {

    public PaymentServiceUnavailableException(String message) {
        super(message);
    }

    public PaymentServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
