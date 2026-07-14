package com.travelagency.packageservice.controller;

import com.travelagency.packageservice.dto.CreatePackageRequest;
import com.travelagency.packageservice.dto.PackageResponse;
import com.travelagency.packageservice.entity.PackageStatus;
import com.travelagency.packageservice.entity.TravelPackage;
import com.travelagency.packageservice.exception.BusinessRuleException;
import com.travelagency.packageservice.service.TravelPackageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// Expone la gestion de paquetes turisticos (Epica 2)
@RestController
@RequestMapping("/api/packages")
public class PackageController {

    private static final String ROLE_ADMIN = "ADMIN";

    private final TravelPackageService service;

    public PackageController(TravelPackageService service) {
        this.service = service;
    }

    // Crear, editar y cambiar estado son operaciones de administracion (Epica 2): exigen
    // el rol de quien pregunta, igual que ReportController en report-service. No hay
    // AccessControlService compartido en este proyecto; se valida inline como alli.
    private void requireAdmin(String role) {
        if (!ROLE_ADMIN.equalsIgnoreCase(role)) {
            throw new BusinessRuleException("Solo un administrador puede gestionar paquetes");
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PackageResponse createPackage(@RequestParam String role, @Valid @RequestBody CreatePackageRequest request) {
        requireAdmin(role);
        TravelPackage created = service.createPackage(request);
        return PackageResponse.fromEntity(created);
    }

    // Listado completo (incluye SOLD_OUT/EXPIRED/CANCELLED): solo ADMIN. Los clientes
    // usan /available o /search, que siguen siendo publicos.
    @GetMapping
    public List<PackageResponse> getAllPackages(@RequestParam String role) {
        requireAdmin(role);
        return service.getAllPackages().stream()
                .map(PackageResponse::fromEntity)
                .toList();
    }

    @GetMapping("/available")
    public List<PackageResponse> getAvailablePackages() {
        return service.getAvailablePackages().stream()
                .map(PackageResponse::fromEntity)
                .toList();
    }

    @GetMapping("/search")
    public List<PackageResponse> searchPackages(
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) LocalDate startDate) {
        return service.searchPackages(destination, minPrice, maxPrice, startDate).stream()
                .map(PackageResponse::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public PackageResponse getPackageById(@PathVariable Long id) {
        return PackageResponse.fromEntity(service.getPackageById(id));
    }

    @PutMapping("/{id}")
    public PackageResponse updatePackage(@PathVariable Long id, @RequestParam String role,
                                          @Valid @RequestBody CreatePackageRequest request) {
        requireAdmin(role);
        TravelPackage updated = service.updatePackage(id, request);
        return PackageResponse.fromEntity(updated);
    }

    @PatchMapping("/{id}/status")
    public PackageResponse changeStatus(@PathVariable Long id, @RequestParam PackageStatus status,
                                         @RequestParam String role) {
        requireAdmin(role);
        TravelPackage updated = service.changeStatus(id, status);
        return PackageResponse.fromEntity(updated);
    }

    // Ajusta cupos reservados (usado por booking-service): delta positivo reserva, negativo libera
    @PatchMapping("/{id}/slots")
    public PackageResponse adjustSlots(@PathVariable Long id, @RequestParam int delta) {
        TravelPackage updated = service.adjustBookedSlots(id, delta);
        return PackageResponse.fromEntity(updated);
    }
}
