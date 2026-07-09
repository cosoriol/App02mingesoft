package com.travelagency.bookingservice.dto;

import com.travelagency.bookingservice.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Datos de salida expuestos por la API para una reserva
public class BookingResponse {

    private final Long id;
    private final String userId;
    private final Long packageId;
    private final String packageName;
    private final String destination;
    private final Integer passengerCount;
    private final BigDecimal baseAmount;
    private final BigDecimal discountPercentage;
    private final BigDecimal discountAmount;
    private final String discountDetails;
    private final BigDecimal totalAmount;
    private final BookingStatus status;
    private final LocalDateTime createdAt;

    public BookingResponse(Long id, String userId, Long packageId, String packageName, String destination,
                            Integer passengerCount, BigDecimal baseAmount, BigDecimal discountPercentage,
                            BigDecimal discountAmount, String discountDetails, BigDecimal totalAmount,
                            BookingStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.packageId = packageId;
        this.packageName = packageName;
        this.destination = destination;
        this.passengerCount = passengerCount;
        this.baseAmount = baseAmount;
        this.discountPercentage = discountPercentage;
        this.discountAmount = discountAmount;
        this.discountDetails = discountDetails;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public Long getPackageId() {
        return packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getDestination() {
        return destination;
    }

    public Integer getPassengerCount() {
        return passengerCount;
    }

    public BigDecimal getBaseAmount() {
        return baseAmount;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public String getDiscountDetails() {
        return discountDetails;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
