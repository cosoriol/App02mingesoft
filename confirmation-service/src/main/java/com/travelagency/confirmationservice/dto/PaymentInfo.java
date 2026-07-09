package com.travelagency.confirmationservice.dto;

import java.time.LocalDateTime;

// Resumen del pago asociado a una reserva, incluido dentro de BookingDetailResponse.
// Es null si la reserva todavia no tiene un pago registrado (p.ej. sigue PENDING).
public class PaymentInfo {

    private final Long paymentId;
    private final LocalDateTime paymentDate;
    private final String cardLastFour;

    public PaymentInfo(Long paymentId, LocalDateTime paymentDate, String cardLastFour) {
        this.paymentId = paymentId;
        this.paymentDate = paymentDate;
        this.cardLastFour = cardLastFour;
    }

    public static PaymentInfo fromResponse(PaymentResponse payment) {
        if (payment == null) {
            return null;
        }
        return new PaymentInfo(payment.getPaymentId(), payment.getPaymentDate(), payment.getCardLastFour());
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public String getCardLastFour() {
        return cardLastFour;
    }
}
