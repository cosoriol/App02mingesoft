package com.travelagency.reportservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Representa el paquete turistico tal como lo expone package-service.
// Se usa unicamente para deserializar la respuesta HTTP (via PackageServiceClient/RestTemplate);
// solo se necesita price (unitPrice del ranking), pero se copian los demas campos por consistencia.
@JsonIgnoreProperties(ignoreUnknown = true)
public class PackageResponse {

    private final Long id;
    private final String name;
    private final String destination;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final BigDecimal price;
    private final Integer totalSlots;
    private final Integer bookedSlots;
    private final Integer availableSlots;
    private final PackageStatus status;
    private final LocalDateTime createdAt;

    @JsonCreator
    public PackageResponse(@JsonProperty("id") Long id,
                            @JsonProperty("name") String name,
                            @JsonProperty("destination") String destination,
                            @JsonProperty("startDate") LocalDate startDate,
                            @JsonProperty("endDate") LocalDate endDate,
                            @JsonProperty("price") BigDecimal price,
                            @JsonProperty("totalSlots") Integer totalSlots,
                            @JsonProperty("bookedSlots") Integer bookedSlots,
                            @JsonProperty("availableSlots") Integer availableSlots,
                            @JsonProperty("status") PackageStatus status,
                            @JsonProperty("createdAt") LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.totalSlots = totalSlots;
        this.bookedSlots = bookedSlots;
        this.availableSlots = availableSlots;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getTotalSlots() {
        return totalSlots;
    }

    public Integer getBookedSlots() {
        return bookedSlots;
    }

    public Integer getAvailableSlots() {
        return availableSlots;
    }

    public PackageStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
