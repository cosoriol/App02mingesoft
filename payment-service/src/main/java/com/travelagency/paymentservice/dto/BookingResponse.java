package com.travelagency.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Representa la reserva tal como la expone booking-service.
// Se usa unicamente para deserializar la respuesta HTTP (via BookingClient/RestTemplate);
// payment-service no persiste ni gestiona estos datos, solo los consulta.
@JsonIgnoreProperties(ignoreUnknown = true)
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

    @JsonCreator
    public BookingResponse(@JsonProperty("id") Long id,
                            @JsonProperty("userId") String userId,
                            @JsonProperty("packageId") Long packageId,
                            @JsonProperty("packageName") String packageName,
                            @JsonProperty("destination") String destination,
                            @JsonProperty("passengerCount") Integer passengerCount,
                            @JsonProperty("baseAmount") BigDecimal baseAmount,
                            @JsonProperty("discountPercentage") BigDecimal discountPercentage,
                            @JsonProperty("discountAmount") BigDecimal discountAmount,
                            @JsonProperty("discountDetails") String discountDetails,
                            @JsonProperty("totalAmount") BigDecimal totalAmount,
                            @JsonProperty("status") BookingStatus status,
                            @JsonProperty("createdAt") LocalDateTime createdAt) {
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
