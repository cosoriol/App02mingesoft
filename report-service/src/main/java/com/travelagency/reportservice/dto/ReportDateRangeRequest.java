package com.travelagency.reportservice.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

// Rango de fechas para generar un reporte. La validacion startDate <= endDate se hace en
// ReportService (no se puede expresar con una sola anotacion sobre dos campos distintos).
public class ReportDateRangeRequest {

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate startDate;

    @NotNull(message = "La fecha de termino es obligatoria")
    private LocalDate endDate;

    private boolean includeCancelled = false;

    public ReportDateRangeRequest() {
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isIncludeCancelled() {
        return includeCancelled;
    }

    public void setIncludeCancelled(boolean includeCancelled) {
        this.includeCancelled = includeCancelled;
    }
}
