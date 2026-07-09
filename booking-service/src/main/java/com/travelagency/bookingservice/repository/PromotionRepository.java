package com.travelagency.bookingservice.repository;

import com.travelagency.bookingservice.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    // Promociones activas y vigentes a la fecha dada
    List<Promotion> findByActiveAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            boolean active, LocalDate startDate, LocalDate endDate);
}
