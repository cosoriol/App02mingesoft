package com.travelagency.reportservice.controller;

import com.travelagency.reportservice.dto.PackageRankingResponse;
import com.travelagency.reportservice.dto.ReportDateRangeRequest;
import com.travelagency.reportservice.dto.SalesReportResponse;
import com.travelagency.reportservice.exception.BusinessRuleException;
import com.travelagency.reportservice.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Expone los reportes de ventas y ranking de paquetes (Epica 7). Acceso exclusivo de ADMIN.
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private static final String ROLE_ADMIN = "ADMIN";

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/sales")
    public SalesReportResponse generateSalesReport(@RequestParam String role,
                                                     @Valid @RequestBody ReportDateRangeRequest request) {
        validateIsAdmin(role);
        return reportService.generateSalesReport(request.getStartDate(), request.getEndDate(), request.isIncludeCancelled());
    }

    @PostMapping("/ranking")
    public PackageRankingResponse generatePackageRanking(@RequestParam String role,
                                                           @Valid @RequestBody ReportDateRangeRequest request) {
        validateIsAdmin(role);
        return reportService.generatePackageRanking(request.getStartDate(), request.getEndDate());
    }

    private void validateIsAdmin(String role) {
        if (!ROLE_ADMIN.equalsIgnoreCase(role)) {
            throw new BusinessRuleException("Solo un administrador puede generar reportes");
        }
    }
}
