package com.travelagency.reportservice.exception;

// Se lanza cuando package-service no esta registrado en Eureka o no responde
public class PackageServiceUnavailableException extends RuntimeException {

    public PackageServiceUnavailableException(String message) {
        super(message);
    }

    public PackageServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
