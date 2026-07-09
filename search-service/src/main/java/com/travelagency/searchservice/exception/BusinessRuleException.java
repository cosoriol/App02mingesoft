package com.travelagency.searchservice.exception;

// Se lanza cuando los filtros de busqueda violan una regla de negocio
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
