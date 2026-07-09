package com.travelagency.reportservice.dto;

import java.math.BigDecimal;

// Una fila del ranking: agregados de todas las reservas de un mismo paquete en el rango consultado
public class PackageRankingItem {

    private final int rank;
    private final String packageName;
    private final String destination;
    private final long bookingCount;
    private final long totalPassengers;
    private final BigDecimal totalAmount;
    private final BigDecimal totalCollected;
    private final BigDecimal unitPrice;

    public PackageRankingItem(int rank, String packageName, String destination, long bookingCount,
                               long totalPassengers, BigDecimal totalAmount, BigDecimal totalCollected,
                               BigDecimal unitPrice) {
        this.rank = rank;
        this.packageName = packageName;
        this.destination = destination;
        this.bookingCount = bookingCount;
        this.totalPassengers = totalPassengers;
        this.totalAmount = totalAmount;
        this.totalCollected = totalCollected;
        this.unitPrice = unitPrice;
    }

    public int getRank() {
        return rank;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getDestination() {
        return destination;
    }

    public long getBookingCount() {
        return bookingCount;
    }

    public long getTotalPassengers() {
        return totalPassengers;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getTotalCollected() {
        return totalCollected;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
}
