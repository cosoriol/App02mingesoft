package com.travelagency.confirmationservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Vista consolidada de una reserva: combina datos de booking-service con el pago (si existe)
// de payment-service, y traduce el estado a un texto legible para el usuario final
public class BookingDetailResponse {

    private final Long id;
    private final String userId;
    private final Long packageId;
    private final String packageName;
    private final String destination;
    private final Integer passengerCount;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final BigDecimal baseAmount;
    private final BigDecimal discountPercentage;
    private final BigDecimal discountAmount;
    private final String discountDetails;
    private final BigDecimal totalAmount;
    private final PaymentInfo paymentInfo;
    private final String status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public BookingDetailResponse(Long id, String userId, Long packageId, String packageName, String destination,
                                  Integer passengerCount, LocalDate startDate, LocalDate endDate,
                                  BigDecimal baseAmount, BigDecimal discountPercentage, BigDecimal discountAmount,
                                  String discountDetails, BigDecimal totalAmount, PaymentInfo paymentInfo,
                                  String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.packageId = packageId;
        this.packageName = packageName;
        this.destination = destination;
        this.passengerCount = passengerCount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.baseAmount = baseAmount;
        this.discountPercentage = discountPercentage;
        this.discountAmount = discountAmount;
        this.discountDetails = discountDetails;
        this.totalAmount = totalAmount;
        this.paymentInfo = paymentInfo;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Combina la reserva (booking-service) con el pago (payment-service, si existe) y el
    // texto legible del estado (ver ConfirmationService.readableStatus)
    public static BookingDetailResponse of(BookingResponse booking, PaymentInfo paymentInfo, String readableStatus) {
        return new BookingDetailResponse(
                booking.getId(),
                booking.getUserId(),
                booking.getPackageId(),
                booking.getPackageName(),
                booking.getDestination(),
                booking.getPassengerCount(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getBaseAmount(),
                booking.getDiscountPercentage(),
                booking.getDiscountAmount(),
                booking.getDiscountDetails(),
                booking.getTotalAmount(),
                paymentInfo,
                readableStatus,
                booking.getCreatedAt(),
                booking.getUpdatedAt());
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
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

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
