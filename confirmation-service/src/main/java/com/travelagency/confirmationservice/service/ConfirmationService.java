package com.travelagency.confirmationservice.service;

import com.travelagency.confirmationservice.client.BookingServiceClient;
import com.travelagency.confirmationservice.client.PaymentServiceClient;
import com.travelagency.confirmationservice.dto.BookingDetailResponse;
import com.travelagency.confirmationservice.dto.BookingResponse;
import com.travelagency.confirmationservice.dto.BookingStatus;
import com.travelagency.confirmationservice.dto.BookingVoucherResponse;
import com.travelagency.confirmationservice.dto.PaymentInfo;
import com.travelagency.confirmationservice.exception.BusinessRuleException;
import com.travelagency.confirmationservice.exception.UnauthorizedAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

// Consolida el seguimiento de una reserva combinando booking-service (datos de la reserva)
// y payment-service (datos del pago, si existe), y expone comprobantes de reservas confirmadas
@Service
@Transactional(readOnly = true)
public class ConfirmationService {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_CLIENT = "CLIENT";

    // Mismo valor convencional usado en el resto de los servicios para las llamadas que
    // un administrador origina hacia booking-service (bypasea la validacion de pertenencia)
    private static final String SYSTEM_USER_ID = "admin";

    private final BookingServiceClient bookingClient;
    private final PaymentServiceClient paymentClient;

    public ConfirmationService(BookingServiceClient bookingClient, PaymentServiceClient paymentClient) {
        this.bookingClient = bookingClient;
        this.paymentClient = paymentClient;
    }

    public BookingDetailResponse getBookingDetail(Long bookingId, String userId, String role) {
        BookingResponse booking = bookingClient.getBookingById(bookingId);

        if (isClient(role)) {
            validateOwnership(booking, userId);
        } else if (!isAdmin(role)) {
            throw new UnauthorizedAccessException("Rol invalido: " + role);
        }

        return toDetail(booking);
    }

    public List<BookingDetailResponse> getMyBookings(String userId) {
        return bookingClient.getBookingsByUserId(userId).stream()
                .map(this::toDetail)
                .toList();
    }

    public List<BookingDetailResponse> getAllBookings(String role) {
        validateIsAdmin(role);
        return bookingClient.getAllBookings().stream()
                .map(this::toDetail)
                .toList();
    }

    // Genera el comprobante de una reserva ya confirmada (y pagada)
    public BookingVoucherResponse generateVoucher(Long bookingId) {
        BookingResponse booking = bookingClient.getBookingById(bookingId);

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BusinessRuleException(
                    "Solo se puede emitir comprobante de una reserva CONFIRMED (actual: " + booking.getStatus() + ")");
        }

        BookingDetailResponse detail = toDetail(booking);
        LocalDateTime issuedDate = LocalDateTime.now();
        String voucherNumber = buildVoucherNumber(bookingId, issuedDate);

        return new BookingVoucherResponse(detail, voucherNumber, issuedDate);
    }

    @Transactional
    public BookingDetailResponse cancelBooking(Long bookingId, String userId, String role) {
        BookingResponse booking = bookingClient.getBookingById(bookingId);

        String effectiveUserId;
        if (isClient(role)) {
            validateOwnership(booking, userId);
            if (booking.getStatus() != BookingStatus.PENDING) {
                throw new BusinessRuleException(
                        "Solo puedes cancelar una reserva en estado PENDING (actual: " + booking.getStatus() + ")");
            }
            effectiveUserId = userId;
        } else if (isAdmin(role)) {
            if (booking.getStatus() == BookingStatus.CANCELLED) {
                throw new BusinessRuleException("La reserva ya esta cancelada");
            }
            effectiveUserId = SYSTEM_USER_ID;
        } else {
            throw new UnauthorizedAccessException("Rol invalido: " + role);
        }

        BookingResponse cancelled = bookingClient.cancelBooking(bookingId, effectiveUserId);
        return toDetail(cancelled);
    }

    private void validateOwnership(BookingResponse booking, String userId) {
        if (userId == null || userId.isBlank() || !booking.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("No tiene permisos para acceder a esta reserva");
        }
    }

    private void validateIsAdmin(String role) {
        if (!isAdmin(role)) {
            throw new UnauthorizedAccessException("Solo un administrador puede listar todas las reservas");
        }
    }

    private boolean isAdmin(String role) {
        return ROLE_ADMIN.equalsIgnoreCase(role);
    }

    private boolean isClient(String role) {
        return ROLE_CLIENT.equalsIgnoreCase(role);
    }

    private BookingDetailResponse toDetail(BookingResponse booking) {
        PaymentInfo paymentInfo = paymentClient.getPaymentByBookingId(booking.getId())
                .map(PaymentInfo::fromResponse)
                .orElse(null);
        return BookingDetailResponse.of(booking, paymentInfo, readableStatus(booking.getStatus()));
    }

    // Traduce el estado tecnico de la reserva a un texto legible para el usuario final
    private String readableStatus(BookingStatus status) {
        if (status == null) {
            return "Desconocido";
        }
        return switch (status) {
            case PENDING -> "Pendiente de pago";
            case CONFIRMED -> "Confirmada";
            case CANCELLED -> "Cancelada";
            case EXPIRED -> "Expirada";
        };
    }

    private String buildVoucherNumber(Long bookingId, LocalDateTime issuedDate) {
        int year = Year.from(issuedDate).getValue();
        return "TA-%d-%05d".formatted(year, bookingId);
    }
}
