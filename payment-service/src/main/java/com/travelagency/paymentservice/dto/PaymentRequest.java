package com.travelagency.paymentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

// Datos de entrada para procesar un pago simulado.
// Las anotaciones de Bean Validation atajan formatos claramente invalidos (400 temprano);
// PaymentValidationService aplica ademas reglas que una anotacion no puede expresar,
// como que la fecha de expiracion no este en el pasado.
public class PaymentRequest {

    @NotNull(message = "El id de la reserva es obligatorio")
    private Long bookingId;

    // No estaba en la lista original de campos, pero sin el no hay nada contra que comparar
    // "el monto debe ser igual a booking.totalAmount" (ver PaymentService.validateAmount):
    // protege contra un monto manipulado o desactualizado en el cliente.
    @NotNull(message = "El monto a pagar es obligatorio")
    @Positive(message = "El monto a pagar debe ser mayor a cero")
    private BigDecimal amount;

    @NotBlank(message = "El numero de tarjeta es obligatorio")
    @Pattern(regexp = "\\d{16}", message = "El numero de tarjeta debe tener exactamente 16 digitos")
    private String cardNumber;

    @NotBlank(message = "La fecha de expiracion es obligatoria")
    @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "La fecha de expiracion debe tener formato MM/YY")
    private String expirationDate;

    @NotBlank(message = "El CVV es obligatorio")
    @Pattern(regexp = "\\d{3}", message = "El CVV debe tener exactamente 3 digitos")
    private String cvv;

    @NotBlank(message = "El nombre del titular es obligatorio")
    private String cardHolderName;

    public PaymentRequest() {
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }
}
