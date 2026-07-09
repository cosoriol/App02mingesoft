package com.travelagency.confirmationservice.controller;

import com.travelagency.confirmationservice.dto.BookingDetailResponse;
import com.travelagency.confirmationservice.dto.BookingVoucherResponse;
import com.travelagency.confirmationservice.service.ConfirmationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Expone el seguimiento y la confirmacion de reservas (Epica 6)
@RestController
@RequestMapping("/api/confirmations")
public class ConfirmationController {

    private final ConfirmationService confirmationService;

    public ConfirmationController(ConfirmationService confirmationService) {
        this.confirmationService = confirmationService;
    }

    @GetMapping("/my-bookings")
    public List<BookingDetailResponse> getMyBookings(@RequestParam String userId) {
        return confirmationService.getMyBookings(userId);
    }

    @GetMapping
    public List<BookingDetailResponse> getAllBookings(@RequestParam String role) {
        return confirmationService.getAllBookings(role);
    }

    @GetMapping("/{bookingId}")
    public BookingDetailResponse getBookingDetail(@PathVariable Long bookingId,
                                                   @RequestParam String userId,
                                                   @RequestParam String role) {
        return confirmationService.getBookingDetail(bookingId, userId, role);
    }

    @GetMapping("/{bookingId}/voucher")
    public BookingVoucherResponse getVoucher(@PathVariable Long bookingId) {
        return confirmationService.generateVoucher(bookingId);
    }

    @PatchMapping("/{bookingId}/cancel")
    public BookingDetailResponse cancelBooking(@PathVariable Long bookingId,
                                                @RequestParam String userId,
                                                @RequestParam String role) {
        return confirmationService.cancelBooking(bookingId, userId, role);
    }
}
