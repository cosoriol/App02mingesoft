package com.travelagency.confirmationservice.exception;

// Se lanza cuando payment-service no esta registrado en Eureka o no responde
// (distinto de "la reserva todavia no tiene pago", que no es un error)
public class PaymentServiceUnavailableException extends RuntimeException {

    public PaymentServiceUnavailableException(String message) {
        super(message);
    }

    public PaymentServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
