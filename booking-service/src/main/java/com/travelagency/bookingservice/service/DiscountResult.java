package com.travelagency.bookingservice.service;

import java.math.BigDecimal;

// Resultado del calculo de descuentos acumulados para una reserva
public record DiscountResult(BigDecimal totalPercentage, BigDecimal discountAmount, String details) {
}
