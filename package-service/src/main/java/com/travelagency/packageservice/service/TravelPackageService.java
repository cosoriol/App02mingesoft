package com.travelagency.packageservice.service;

import com.travelagency.packageservice.dto.CreatePackageRequest;
import com.travelagency.packageservice.entity.PackageStatus;
import com.travelagency.packageservice.entity.TravelPackage;
import com.travelagency.packageservice.exception.BusinessRuleException;
import com.travelagency.packageservice.exception.ResourceNotFoundException;
import com.travelagency.packageservice.repository.TravelPackageRepository;
import com.travelagency.packageservice.repository.TravelPackageSpecifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class TravelPackageService {

    private final TravelPackageRepository repository;

    public TravelPackageService(TravelPackageRepository repository) {
        this.repository = repository;
    }

    // Crea un nuevo paquete turistico validando las reglas de negocio
    public TravelPackage createPackage(CreatePackageRequest request) {
        validateDates(request.getStartDate(), request.getEndDate());
        validatePrice(request.getPrice());
        validateTotalSlots(request.getTotalSlots());

        TravelPackage travelPackage = new TravelPackage();
        travelPackage.setName(request.getName());
        travelPackage.setDestination(request.getDestination());
        travelPackage.setDescription(request.getDescription());
        travelPackage.setStartDate(request.getStartDate());
        travelPackage.setEndDate(request.getEndDate());
        travelPackage.setPrice(request.getPrice());
        travelPackage.setTotalSlots(request.getTotalSlots());
        travelPackage.setBookedSlots(0);
        travelPackage.setIncludedServices(request.getIncludedServices());
        travelPackage.setRestrictions(request.getRestrictions());
        travelPackage.setTravelType(request.getTravelType());
        travelPackage.setSeason(request.getSeason());
        travelPackage.setStatus(PackageStatus.AVAILABLE);
        travelPackage.setDurationDays(calculateDurationDays(request.getStartDate(), request.getEndDate()));

        return repository.save(travelPackage);
    }

    @Transactional(readOnly = true)
    public TravelPackage getPackageById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un paquete con id " + id));
    }

    @Transactional(readOnly = true)
    public List<TravelPackage> getAllPackages() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<TravelPackage> getAvailablePackages() {
        return repository.findByStatus(PackageStatus.AVAILABLE);
    }

    @Transactional(readOnly = true)
    public List<TravelPackage> searchPackages(String destination, BigDecimal minPrice, BigDecimal maxPrice, LocalDate startDate) {
        return repository.findAll(TravelPackageSpecifications.withFilters(destination, minPrice, maxPrice, startDate));
    }

    // Actualiza un paquete existente validando que no se rompan reservas ya confirmadas
    public TravelPackage updatePackage(Long id, CreatePackageRequest request) {
        TravelPackage travelPackage = getPackageById(id);

        validateDates(request.getStartDate(), request.getEndDate());
        validatePrice(request.getPrice());
        validateTotalSlots(request.getTotalSlots());

        if (request.getTotalSlots() < travelPackage.getBookedSlots()) {
            throw new BusinessRuleException(
                    "No se puede reducir el total de cupos por debajo de los cupos ya reservados ("
                            + travelPackage.getBookedSlots() + ")");
        }

        travelPackage.setName(request.getName());
        travelPackage.setDestination(request.getDestination());
        travelPackage.setDescription(request.getDescription());
        travelPackage.setStartDate(request.getStartDate());
        travelPackage.setEndDate(request.getEndDate());
        travelPackage.setPrice(request.getPrice());
        travelPackage.setTotalSlots(request.getTotalSlots());
        travelPackage.setIncludedServices(request.getIncludedServices());
        travelPackage.setRestrictions(request.getRestrictions());
        travelPackage.setTravelType(request.getTravelType());
        travelPackage.setSeason(request.getSeason());
        travelPackage.setDurationDays(calculateDurationDays(request.getStartDate(), request.getEndDate()));

        return repository.save(travelPackage);
    }

    // Cambia el estado del paquete, respetando que uno cancelado no pueda modificarse
    public TravelPackage changeStatus(Long id, PackageStatus newStatus) {
        TravelPackage travelPackage = getPackageById(id);

        if (travelPackage.getStatus() == PackageStatus.CANCELLED) {
            throw new BusinessRuleException("No se puede cambiar el estado de un paquete cancelado");
        }

        travelPackage.setStatus(newStatus);
        return repository.save(travelPackage);
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (endDate == null || startDate == null || !endDate.isAfter(startDate)) {
            throw new BusinessRuleException("La fecha de termino debe ser posterior a la fecha de inicio");
        }
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("El precio debe ser mayor a cero");
        }
    }

    private void validateTotalSlots(Integer totalSlots) {
        if (totalSlots == null || totalSlots <= 0) {
            throw new BusinessRuleException("El total de cupos debe ser mayor a cero");
        }
    }

    // Duracion del viaje en dias, contando el dia de inicio y el dia de termino
    private int calculateDurationDays(LocalDate startDate, LocalDate endDate) {
        return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
}
