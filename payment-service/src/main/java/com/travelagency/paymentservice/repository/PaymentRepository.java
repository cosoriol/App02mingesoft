package com.travelagency.paymentservice.repository;

import com.travelagency.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBookingId(Long bookingId);

    boolean existsByBookingId(Long bookingId);

    // Consulta masiva usada por report-service para evitar una llamada HTTP por reserva
    List<Payment> findByBookingIdIn(List<Long> bookingIds);
}
