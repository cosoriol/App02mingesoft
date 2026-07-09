package com.travelagency.searchservice.repository;

import com.travelagency.searchservice.entity.TravelPackage;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

// Arma dinamicamente los predicados de busqueda: cada filtro solo se agrega si viene informado,
// evitando construir SQL con parametros nulos (fuente de comportamiento inconsistente en PostgreSQL)
public final class TravelPackageSpecifications {

    private TravelPackageSpecifications() {
    }

    public static Specification<TravelPackage> withFilters(String destination, BigDecimal minPrice,
                                                             BigDecimal maxPrice, LocalDate startDate) {
        Specification<TravelPackage> spec = Specification.where(null);

        if (destination != null && !destination.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("destination")), "%" + destination.toLowerCase() + "%"));
        }
        if (minPrice != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }
        if (startDate != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startDate"), startDate));
        }

        return spec;
    }
}
