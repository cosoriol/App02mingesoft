package com.travelagency.confirmationservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Representa el pago tal como lo expone payment-service.
// Se usa unicamente para deserializar la respuesta HTTP (via PaymentServiceClient/RestTemplate).
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponse {

    private final Long paymentId;
    private final Long bookingId;
    private final BigDecimal amount;
    private final PaymentMethod paymentMethod;
    private final String cardLastFour;
    private final String paymentStatus;
    private final LocalDateTime paymentDate;

    @JsonCreator
    public PaymentResponse(@JsonProperty("paymentId") Long paymentId,
                            @JsonProperty("bookingId") Long bookingId,
                            @JsonProperty("amount") BigDecimal amount,
                            @JsonProperty("paymentMethod") PaymentMethod paymentMethod,
                            @JsonProperty("cardLastFour") String cardLastFour,
                            @JsonProperty("paymentStatus") String paymentStatus,
                            @JsonProperty("paymentDate") LocalDateTime paymentDate) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.cardLastFour = cardLastFour;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
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
}
