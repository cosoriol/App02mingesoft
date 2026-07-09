package com.travelagency.searchservice.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

// Datos de entrada para crear o actualizar un paquete turistico
public class CreatePackageRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El destino es obligatorio")
    private String destination;

    @NotBlank(message = "La descripcion es obligatoria")
    private String description;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio no puede ser en el pasado")
    private LocalDate startDate;

    @NotNull(message = "La fecha de termino es obligatoria")
    private LocalDate endDate;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    private BigDecimal price;

    @NotNull(message = "El total de cupos es obligatorio")
    @Positive(message = "El total de cupos debe ser mayor a cero")
    private Integer totalSlots;

    private String includedServices;

    private String restrictions;

    @NotBlank(message = "El tipo de viaje es obligatorio")
    private String travelType;

    @NotBlank(message = "La temporada es obligatoria")
    private String season;

    public CreatePackageRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getTotalSlots() {
        return totalSlots;
    }

    public void setTotalSlots(Integer totalSlots) {
        this.totalSlots = totalSlots;
    }

    public String getIncludedServices() {
        return includedServices;
    }

    public void setIncludedServices(String includedServices) {
        this.includedServices = includedServices;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    public String getTravelType() {
        return travelType;
    }

    public void setTravelType(String travelType) {
        this.travelType = travelType;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }
}
