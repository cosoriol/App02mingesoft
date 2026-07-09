package com.travelagency.paymentservice.controller;

import com.travelagency.paymentservice.dto.PaymentRequest;
import com.travelagency.paymentservice.dto.PaymentResponse;
import com.travelagency.paymentservice.dto.PaymentSummaryResponse;
import com.travelagency.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Expone el proceso de pago simulado de reservas (Epica 5)
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse processPayment(@Valid @RequestBody PaymentRequest request) {
        return paymentService.processPayment(request);
    }

    @GetMapping("/booking/{bookingId}")
    public PaymentResponse getPaymentByBookingId(@PathVariable Long bookingId) {
        return paymentService.getPaymentByBookingId(bookingId);
    }

    @GetMapping("/summary/{bookingId}")
    public PaymentSummaryResponse getPaymentSummary(@PathVariable Long bookingId) {
        return paymentService.getPaymentSummary(bookingId);
    }

    // Llamado por report-service para no hacer una consulta HTTP por cada reserva
    @GetMapping("/by-bookings")
    public List<PaymentResponse> getPaymentsByBookingIds(@RequestParam List<Long> bookingIds) {
        return paymentService.getPaymentsByBookingIds(bookingIds);
    }
}
