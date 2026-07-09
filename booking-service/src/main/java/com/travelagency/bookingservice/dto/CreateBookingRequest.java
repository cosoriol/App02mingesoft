package com.travelagency.bookingservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Datos de entrada para crear una reserva.
// El userId real de la operacion se toma del query param del endpoint (representa al usuario
// autenticado), no de este campo: nunca se confia en un identificador de usuario enviado en el
// cuerpo de la peticion.
public class CreateBookingRequest {

    @NotNull(message = "El id del paquete es obligatorio")
    private Long packageId;

    @NotNull(message = "La cantidad de pasajeros es obligatoria")
    @Positive(message = "La cantidad de pasajeros debe ser mayor a cero")
    private Integer passengerCount;

    private String userId;

    public CreateBookingRequest() {
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public Integer getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(Integer passengerCount) {
        this.passengerCount = passengerCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
