package com.travelagency.reportservice.dto;

import java.math.BigDecimal;
import java.util.Map;

public class SalesReportSummary {

    private final long totalBookings;
    private final long totalPassengers;
    private final BigDecimal totalSalesAmount;
    private final BigDecimal totalCollectedAmount;
    private final Map<String, Long> bookingsByStatus;

    public SalesReportSummary(long totalBookings, long totalPassengers, BigDecimal totalSalesAmount,
                               BigDecimal totalCollectedAmount, Map<String, Long> bookingsByStatus) {
        this.totalBookings = totalBookings;
        this.totalPassengers = totalPassengers;
        this.totalSalesAmount = totalSalesAmount;
        this.totalCollectedAmount = totalCollectedAmount;
        this.bookingsByStatus = bookingsByStatus;
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public long getTotalPassengers() {
        return totalPassengers;
    }

    public BigDecimal getTotalSalesAmount() {
        return totalSalesAmount;
    }

    public BigDecimal getTotalCollectedAmount() {
        return totalCollectedAmount;
    }

    public Map<String, Long> getBookingsByStatus() {
        return bookingsByStatus;
    }
}
