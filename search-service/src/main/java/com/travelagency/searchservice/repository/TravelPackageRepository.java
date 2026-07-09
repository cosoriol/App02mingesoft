package com.travelagency.searchservice.repository;

import com.travelagency.searchservice.entity.PackageStatus;
import com.travelagency.searchservice.entity.TravelPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

// JpaSpecificationExecutor permite ejecutar las busquedas con filtros opcionales armadas
// dinamicamente (ver TravelPackageSpecifications), en vez de un @Query estatico con
// parametros que pueden venir null: ese enfoque resulto ser fragil en PostgreSQL
// ("could not determine data type" / cast a bytea segun que combinacion de parametros
// llegara null).
public interface TravelPackageRepository extends JpaRepository<TravelPackage, Long>,
        JpaSpecificationExecutor<TravelPackage> {

    List<TravelPackage> findByStatus(PackageStatus status);

    List<TravelPackage> findByDestinationContainingIgnoreCaseAndStatus(String destination, PackageStatus status);
}
