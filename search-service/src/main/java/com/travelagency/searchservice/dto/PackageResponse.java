package com.travelagency.searchservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.travelagency.searchservice.entity.PackageStatus;
import com.travelagency.searchservice.entity.TravelPackage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Datos de un paquete turistico, tal como los expone package-service.
// Se usa tanto para deserializar la respuesta de package-service (via RestTemplate/Jackson)
// como para responder desde los endpoints propios de search-service.
public class PackageResponse {

    private final Long id;
    private final String name;
    private final String destination;
    private final String description;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final BigDecimal price;
    private final Integer totalSlots;
    private final Integer bookedSlots;
    private final String includedServices;
    private final String restrictions;
    private final String travelType;
    private final String season;
    private final PackageStatus status;
    private final Integer durationDays;
    private final LocalDateTime createdAt;

    @JsonCreator
    public PackageResponse(@JsonProperty("id") Long id,
                            @JsonProperty("name") String name,
                            @JsonProperty("destination") String destination,
                            @JsonProperty("description") String description,
                            @JsonProperty("startDate") LocalDate startDate,
                            @JsonProperty("endDate") LocalDate endDate,
                            @JsonProperty("price") BigDecimal price,
                            @JsonProperty("totalSlots") Integer totalSlots,
                            @JsonProperty("bookedSlots") Integer bookedSlots,
                            @JsonProperty("includedServices") String includedServices,
                            @JsonProperty("restrictions") String restrictions,
                            @JsonProperty("travelType") String travelType,
                            @JsonProperty("season") String season,
                            @JsonProperty("status") PackageStatus status,
                            @JsonProperty("durationDays") Integer durationDays,
                            @JsonProperty("createdAt") LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.destination = destination;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.totalSlots = totalSlots;
        this.bookedSlots = bookedSlots;
        this.includedServices = includedServices;
        this.restrictions = restrictions;
        this.travelType = travelType;
        this.season = season;
        this.status = status;
        this.durationDays = durationDays;
        this.createdAt = createdAt;
    }

    // Mapea la entidad local hacia el DTO de respuesta
    public static PackageResponse fromEntity(TravelPackage travelPackage) {
        return new PackageResponse(
                travelPackage.getId(),
                travelPackage.getName(),
                travelPackage.getDestination(),
                travelPackage.getDescription(),
                travelPackage.getStartDate(),
                travelPackage.getEndDate(),
                travelPackage.getPrice(),
                travelPackage.getTotalSlots(),
                travelPackage.getBookedSlots(),
                travelPackage.getIncludedServices(),
                travelPackage.getRestrictions(),
                travelPackage.getTravelType(),
                travelPackage.getSeason(),
                travelPackage.getStatus(),
                travelPackage.getDurationDays(),
                travelPackage.getCreatedAt());
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

    public String getDescription() {
        return description;
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

    public int getAvailableSlots() {
        return totalSlots - bookedSlots;
    }

    public String getIncludedServices() {
        return includedServices;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public String getTravelType() {
        return travelType;
    }

    public String getSeason() {
        return season;
    }

    public PackageStatus getStatus() {
        return status;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
