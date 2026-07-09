package com.travelagency.reportservice.dto;

import java.math.BigDecimal;

public class PackageRankingSummary {

    private final long totalPackagesWithSales;
    private final long totalBookings;
    private final long totalPassengers;
    private final BigDecimal totalAmount;

    public PackageRankingSummary(long totalPackagesWithSales, long totalBookings, long totalPassengers,
                                  BigDecimal totalAmount) {
        this.totalPackagesWithSales = totalPackagesWithSales;
        this.totalBookings = totalBookings;
        this.totalPassengers = totalPassengers;
        this.totalAmount = totalAmount;
    }

    public long getTotalPackagesWithSales() {
        return totalPackagesWithSales;
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public long getTotalPassengers() {
        return totalPassengers;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
