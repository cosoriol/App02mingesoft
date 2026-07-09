package com.travelagency.paymentservice.dto;

import com.travelagency.paymentservice.entity.Payment;
import com.travelagency.paymentservice.entity.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Datos de salida expuestos por la API tras procesar un pago
public class PaymentResponse {

    private final Long paymentId;
    private final Long bookingId;
    private final BigDecimal amount;
    private final PaymentMethod paymentMethod;
    private final String cardLastFour;
    private final String paymentStatus;
    private final LocalDateTime paymentDate;
    private final String message;

    public PaymentResponse(Long paymentId, Long bookingId, BigDecimal amount, PaymentMethod paymentMethod,
                            String cardLastFour, String paymentStatus, LocalDateTime paymentDate, String message) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.cardLastFour = cardLastFour;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
        this.message = message;
    }

    // Mapea la entidad hacia el DTO de respuesta, con el mensaje de confirmacion del pago simulado
    public static PaymentResponse fromEntity(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getBookingId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getCardLastFour(),
                payment.getPaymentStatus(),
                payment.getPaymentDate(),
                "Pago procesado exitosamente");
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public String getCardLastFour() {
        return cardLastFour;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public String getMessage() {
        return message;
    }
}
