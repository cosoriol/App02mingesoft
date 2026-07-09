package com.travelagency.paymentservice.service;

import com.travelagency.paymentservice.client.BookingClient;
import com.travelagency.paymentservice.dto.BookingResponse;
import com.travelagency.paymentservice.dto.BookingStatus;
import com.travelagency.paymentservice.dto.PaymentRequest;
import com.travelagency.paymentservice.dto.PaymentResponse;
import com.travelagency.paymentservice.dto.PaymentSummaryResponse;
import com.travelagency.paymentservice.entity.Payment;
import com.travelagency.paymentservice.entity.PaymentMethod;
import com.travelagency.paymentservice.exception.BusinessRuleException;
import com.travelagency.paymentservice.exception.ResourceNotFoundException;
import com.travelagency.paymentservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

// Orquesta el pago simulado: valida la reserva contra booking-service, aprueba el pago
// (siempre exitoso) y confirma la reserva
@Service
@Transactional
public class PaymentService {

    private final PaymentValidationService validationService;
    private final BookingClient bookingClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentValidationService validationService, BookingClient bookingClient,
                           PaymentRepository paymentRepository) {
        this.validationService = validationService;
        this.bookingClient = bookingClient;
        this.paymentRepository = paymentRepository;
    }

    public PaymentResponse processPayment(PaymentRequest request) {
        validationService.validateCardFormat(request);

        BookingResponse booking = bookingClient.getBooking(request.getBookingId());
        validateBookingPayable(booking);
        validateNoExistingPayment(booking.getId());
        validateAmount(request.getAmount(), booking.getTotalAmount());

        Payment payment = new Payment();
        payment.setBookingId(booking.getId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        payment.setCardLastFour(lastFourDigits(request.getCardNumber()));
        payment.setPaymentStatus(Payment.STATUS_APPROVED);

        Payment saved = paymentRepository.save(payment);

        // El pago simulado siempre aprueba, asi que la reserva se confirma de inmediato
        bookingClient.confirmBooking(booking.getId());

        return PaymentResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByBookingId(Long bookingId) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un pago para la reserva " + bookingId));
        return PaymentResponse.fromEntity(payment);
    }

    // Vista previa del monto a cobrar, antes de que el cliente ingrese los datos de la tarjeta
    @Transactional(readOnly = true)
    public PaymentSummaryResponse getPaymentSummary(Long bookingId) {
        BookingResponse booking = bookingClient.getBooking(bookingId);
        return PaymentSummaryResponse.fromBooking(booking);
    }

    private void validateBookingPayable(BookingResponse booking) {
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BusinessRuleException(
                    "Solo se puede pagar una reserva en estado PENDING (actual: " + booking.getStatus() + ")");
        }
    }

    private void validateNoExistingPayment(Long bookingId) {
        if (paymentRepository.existsByBookingId(bookingId)) {
            throw new BusinessRuleException("Ya existe un pago registrado para la reserva " + bookingId);
        }
    }

    // No se aceptan pagos parciales: el monto debe coincidir exactamente con el total de la reserva
    private void validateAmount(BigDecimal requestedAmount, BigDecimal totalAmount) {
        if (requestedAmount == null || requestedAmount.compareTo(totalAmount) != 0) {
            throw new BusinessRuleException(
                    "El monto del pago (" + requestedAmount + ") debe ser igual al total de la reserva (" + totalAmount + ")");
        }
    }

    private String lastFourDigits(String cardNumber) {
        return cardNumber.substring(cardNumber.length() - 4);
    }
}
