package com.travelagency.packageservice.dto;

import com.travelagency.packageservice.entity.PackageStatus;
import com.travelagency.packageservice.entity.TravelPackage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Datos de salida expuestos por la API para un paquete turistico
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

    public PackageResponse(Long id, String name, String destination, String description,
                            LocalDate startDate, LocalDate endDate, BigDecimal price,
                            Integer totalSlots, Integer bookedSlots, String includedServices,
                            String restrictions, String travelType, String season,
                            PackageStatus status, Integer durationDays, LocalDateTime createdAt) {
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

    // Mapea la entidad hacia el DTO de respuesta
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
