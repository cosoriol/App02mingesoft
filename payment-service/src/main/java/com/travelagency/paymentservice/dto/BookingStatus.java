package com.travelagency.paymentservice.dto;

// Copia del enum de estados expuesto por booking-service, usado para deserializar sus respuestas
public enum BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    EXPIRED
}
