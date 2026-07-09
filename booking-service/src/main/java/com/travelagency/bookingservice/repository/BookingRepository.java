package com.travelagency.bookingservice.repository;

import com.travelagency.bookingservice.entity.Booking;
import com.travelagency.bookingservice.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(String userId);

    List<Booking> findByUserIdAndStatus(String userId, BookingStatus status);

    // Cantidad de reservas de un usuario en un estado dado (usado para el descuento de cliente frecuente)
    long countByUserIdAndStatus(String userId, BookingStatus status);

    // Cantidad de reservas recientes de un usuario en ciertos estados (usado para el descuento multi-paquete)
    long countByUserIdAndCreatedAtAfterAndStatusIn(String userId, LocalDateTime after, Collection<BookingStatus> statuses);

    // Reservas PENDING vencidas, para el proceso programado que las marca como EXPIRED
    List<Booking> findByStatusAndCreatedAtBefore(BookingStatus status, LocalDateTime cutoff);

    // Reservas creadas dentro de un rango, para reportes
    @Query("SELECT b FROM Booking b WHERE b.createdAt BETWEEN :start AND :end")
    List<Booking> findByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
