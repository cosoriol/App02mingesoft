package com.travelagency.reportservice.dto;

// Copia del enum de estados expuesto por package-service, usado para deserializar sus respuestas
public enum PackageStatus {
    AVAILABLE,
    SOLD_OUT,
    EXPIRED,
    CANCELLED
}
