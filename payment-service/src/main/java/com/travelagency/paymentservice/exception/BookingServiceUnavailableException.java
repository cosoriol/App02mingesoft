package com.travelagency.paymentservice.exception;

// Se lanza cuando booking-service no esta registrado en Eureka o no responde
public class BookingServiceUnavailableException extends RuntimeException {

    public BookingServiceUnavailableException(String message) {
        super(message);
    }

    public BookingServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
