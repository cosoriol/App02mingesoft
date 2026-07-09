package com.travelagency.confirmationservice.repository;

import com.travelagency.confirmationservice.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// findAll() y findById() ya los aporta JpaRepository
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(String userId);
}
