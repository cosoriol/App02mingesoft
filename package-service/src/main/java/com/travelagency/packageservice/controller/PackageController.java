package com.travelagency.packageservice.controller;

import com.travelagency.packageservice.dto.CreatePackageRequest;
import com.travelagency.packageservice.dto.PackageResponse;
import com.travelagency.packageservice.entity.PackageStatus;
import com.travelagency.packageservice.entity.TravelPackage;
import com.travelagency.packageservice.service.TravelPackageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin
public class PackageController {

    private final TravelPackageService service;

    public PackageController(TravelPackageService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PackageResponse createPackage(@Valid @RequestBody CreatePackageRequest request) {
        TravelPackage created = service.createPackage(request);
        return PackageResponse.fromEntity(created);
    }

    @GetMapping
    public List<PackageResponse> getAllPackages() {
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
    public PackageResponse updatePackage(@PathVariable Long id, @Valid @RequestBody CreatePackageRequest request) {
        TravelPackage updated = service.updatePackage(id, request);
        return PackageResponse.fromEntity(updated);
    }

    @PatchMapping("/{id}/status")
    public PackageResponse changeStatus(@PathVariable Long id, @RequestParam PackageStatus status) {
        TravelPackage updated = service.changeStatus(id, status);
        return PackageResponse.fromEntity(updated);
    }
}
