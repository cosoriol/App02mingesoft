package com.travelagency.searchservice.service;

import com.travelagency.searchservice.client.SearchClient;
import com.travelagency.searchservice.dto.PackageResponse;
import com.travelagency.searchservice.dto.SearchRequest;
import com.travelagency.searchservice.entity.PackageStatus;
import com.travelagency.searchservice.exception.BusinessRuleException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// Logica de busqueda de paquetes: delega los datos a package-service y aplica
// las reglas de negocio propias de la busqueda (validaciones y filtro de disponibilidad)
@Service
@Transactional(readOnly = true)
public class SearchService {

    private final SearchClient searchClient;

    public SearchService(SearchClient searchClient) {
        this.searchClient = searchClient;
    }

    // Busca paquetes disponibles por destino (coincidencia parcial, sin distinguir mayusculas)
    public List<PackageResponse> searchByDestination(String destination) {
        return onlyAvailable(searchClient.searchPackages(destination, null, null, null));
    }

    // Busca paquetes disponibles dentro de un rango de precio
    public List<PackageResponse> searchByPrice(BigDecimal minPrice, BigDecimal maxPrice) {
        validatePriceRange(minPrice, maxPrice);
        return onlyAvailable(searchClient.searchPackages(null, minPrice, maxPrice, null));
    }

    // Busca paquetes disponibles cuyo viaje completo (inicio y termino) caiga dentro del rango dado.
    // package-service solo filtra por "startDate >= X" en su API, asi que el limite superior
    // (endDate) se aplica aqui, sobre los resultados ya traidos.
    public List<PackageResponse> searchByDateRange(LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        return onlyAvailable(searchClient.searchPackages(null, null, null, startDate)).stream()
                .filter(p -> !p.getEndDate().isAfter(endDate))
                .toList();
    }

    // Busqueda avanzada combinando todos los filtros disponibles
    public List<PackageResponse> searchAll(SearchRequest filters) {
        validatePriceRange(filters.getMinPrice(), filters.getMaxPrice());

        List<PackageResponse> results = onlyAvailable(searchClient.searchPackages(
                filters.getDestination(), filters.getMinPrice(), filters.getMaxPrice(), filters.getStartDate()));

        return results.stream()
                .filter(p -> matches(filters.getTravelType(), p.getTravelType()))
                .filter(p -> matches(filters.getSeason(), p.getSeason()))
                .toList();
    }

    // Solo se muestran paquetes con cupo y vigentes para reserva
    private List<PackageResponse> onlyAvailable(List<PackageResponse> packages) {
        return packages.stream()
                .filter(p -> p.getStatus() == PackageStatus.AVAILABLE)
                .toList();
    }

    private boolean matches(String filterValue, String actualValue) {
        return filterValue == null || filterValue.equalsIgnoreCase(actualValue);
    }

    private void validatePriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new BusinessRuleException("El precio minimo no puede ser mayor al precio maximo");
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BusinessRuleException("La fecha de inicio no puede ser posterior a la fecha de termino");
        }
    }
}
