package com.travelagency.paymentservice.dto;

import java.math.BigDecimal;

// Resumen de una reserva para mostrar antes de pagar (preview del monto a cobrar)
public class PaymentSummaryResponse {

    private final Long bookingId;
    private final String packageName;
    private final String destination;
    private final Integer passengerCount;
    private final BigDecimal baseAmount;
    private final BigDecimal discountPercentage;
    private final String discountDetails;
    private final BigDecimal totalAmount;

    public PaymentSummaryResponse(Long bookingId, String packageName, String destination, Integer passengerCount,
                                   BigDecimal baseAmount, BigDecimal discountPercentage, String discountDetails,
                                   BigDecimal totalAmount) {
        this.bookingId = bookingId;
        this.packageName = packageName;
        this.destination = destination;
        this.passengerCount = passengerCount;
        this.baseAmount = baseAmount;
        this.discountPercentage = discountPercentage;
        this.discountDetails = discountDetails;
        this.totalAmount = totalAmount;
    }

    // Mapea la respuesta de booking-service hacia el resumen pre-pago
    public static PaymentSummaryResponse fromBooking(BookingResponse booking) {
        return new PaymentSummaryResponse(
                booking.getId(),
                booking.getPackageName(),
                booking.getDestination(),
                booking.getPassengerCount(),
                booking.getBaseAmount(),
                booking.getDiscountPercentage(),
                booking.getDiscountDetails(),
                booking.getTotalAmount());
    }

    public Long getBookingId() {
        return bookingId;
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

    public String getDiscountDetails() {
        return discountDetails;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
