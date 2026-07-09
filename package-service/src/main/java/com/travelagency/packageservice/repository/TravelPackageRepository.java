package com.travelagency.packageservice.repository;

import com.travelagency.packageservice.entity.PackageStatus;
import com.travelagency.packageservice.entity.TravelPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TravelPackageRepository extends JpaRepository<TravelPackage, Long> {

    List<TravelPackage> findByStatus(PackageStatus status);

    List<TravelPackage> findByDestinationContainingIgnoreCaseAndStatus(String destination, PackageStatus status);

    // Busqueda avanzada con filtros opcionales (cualquier parametro puede venir null)
    // destination y startDate necesitan CAST explicito porque, al ir solos, PostgreSQL
    // no logra inferir su tipo en la comparacion "IS NULL" (termina infiriendo bytea).
    // minPrice/maxPrice NO deben llevar CAST: p.price >= :minPrice ya le da a Postgres
    // el tipo numeric por contexto, y "CAST(? AS numeric)" falla con un parametro null
    // ("cannot cast type bytea to numeric") porque ahi no hay ese contexto.
    @Query("SELECT p FROM TravelPackage p WHERE "
            + "(CAST(:destination AS string) IS NULL OR LOWER(p.destination) LIKE LOWER(CONCAT('%', CAST(:destination AS string), '%'))) AND "
            + "(:minPrice IS NULL OR p.price >= :minPrice) AND "
            + "(:maxPrice IS NULL OR p.price <= :maxPrice) AND "
            + "(CAST(:startDate AS date) IS NULL OR p.startDate >= CAST(:startDate AS date))")
    List<TravelPackage> searchPackages(
            @Param("destination") String destination,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("startDate") LocalDate startDate);
}
