package com.travelagency.reportservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

// Una fila del reporte de ventas: una reserva dentro del rango consultado
public class SalesReportItem {

    private final LocalDate fecha;
    private final String clientName;
    private final String clientEmail;
    private final String packageName;
    private final String destination;
    private final Integer passengerCount;
    private final BigDecimal baseAmount;
    private final BigDecimal discountPercentage;
    private final BigDecimal discountAmount;
    private final BigDecimal totalAmount;
    private final BigDecimal amountPaid;
    private final String status;

    public SalesReportItem(LocalDate fecha, String clientName, String clientEmail, String packageName,
                            String destination, Integer passengerCount, BigDecimal baseAmount,
                            BigDecimal discountPercentage, BigDecimal discountAmount, BigDecimal totalAmount,
                            BigDecimal amountPaid, String status) {
        this.fecha = fecha;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.packageName = packageName;
        this.destination = destination;
        this.passengerCount = passengerCount;
        this.baseAmount = baseAmount;
        this.discountPercentage = discountPercentage;
        this.discountAmount = discountAmount;
        this.totalAmount = totalAmount;
        this.amountPaid = amountPaid;
        this.status = status;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientEmail() {
        return clientEmail;
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

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public String getStatus() {
        return status;
    }
}
