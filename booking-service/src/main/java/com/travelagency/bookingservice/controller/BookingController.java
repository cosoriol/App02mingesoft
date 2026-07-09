package com.travelagency.bookingservice.controller;

import com.travelagency.bookingservice.dto.BookingResponse;
import com.travelagency.bookingservice.dto.CreateBookingRequest;
import com.travelagency.bookingservice.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Expone el proceso de reserva de paquetes turisticos (Epica 4)
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@RequestParam String userId, @Valid @RequestBody CreateBookingRequest request) {
        return bookingService.createBooking(userId, request);
    }

    // Solo administradores (ver BookingService.validateIsAdmin)
    @GetMapping
    public List<BookingResponse> getAllBookings(@RequestParam String userId) {
        return bookingService.getAllBookings(userId);
    }

    @GetMapping("/user/{userId}")
    public List<BookingResponse> getBookingsByUser(@PathVariable String userId) {
        return bookingService.getBookingsByUser(userId);
    }

    @GetMapping("/{id}")
    public BookingResponse getBookingById(@PathVariable Long id, @RequestParam String userId) {
        return bookingService.getBookingById(id, userId);
    }

    @PatchMapping("/{id}/cancel")
    public BookingResponse cancelBooking(@PathVariable Long id, @RequestParam String userId) {
        return bookingService.cancelBooking(id, userId);
    }
}
