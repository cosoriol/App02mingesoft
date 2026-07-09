package com.travelagency.searchservice.config;

import com.travelagency.searchservice.exception.BusinessRuleException;
import com.travelagency.searchservice.exception.PackageServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

// Traduce las excepciones de la aplicacion a respuestas HTTP consistentes
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessRule(BusinessRuleException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildBody(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    // package-service caido o no registrado en Eureka: no es un error del cliente, sino de disponibilidad
    @ExceptionHandler(PackageServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handlePackageServiceUnavailable(PackageServiceUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(buildBody(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage()));
    }

    private Map<String, Object> buildBody(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return body;
    }
}
