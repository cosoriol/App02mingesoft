package com.travelagency.bookingservice.service;

import com.travelagency.bookingservice.client.BookingClient;
import com.travelagency.bookingservice.dto.BookingResponse;
import com.travelagency.bookingservice.dto.CreateBookingRequest;
import com.travelagency.bookingservice.dto.PackageResponse;
import com.travelagency.bookingservice.dto.PackageStatus;
import com.travelagency.bookingservice.entity.Booking;
import com.travelagency.bookingservice.entity.BookingStatus;
import com.travelagency.bookingservice.exception.BusinessRuleException;
import com.travelagency.bookingservice.exception.PackageServiceUnavailableException;
import com.travelagency.bookingservice.exception.ResourceNotFoundException;
import com.travelagency.bookingservice.exception.UnauthorizedAccessException;
import com.travelagency.bookingservice.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

// Orquesta el proceso de reserva: valida el paquete contra package-service, calcula descuentos
// y administra el ciclo de vida de la reserva (pendiente, cancelada, expirada)
@Service
@Transactional
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    // Identificador de usuario que representa al rol administrador.
    // No hay integracion real con Keycloak todavia: es un valor convencional hasta que
    // se incorpore autenticacion/roles reales.
    private static final String ADMIN_USER_ID = "admin";

    private static final int EXPIRATION_MINUTES = 30;

    private final BookingRepository bookingRepository;
    private final DiscountService discountService;
    private final BookingClient bookingClient;

    public BookingService(BookingRepository bookingRepository, DiscountService discountService,
                           BookingClient bookingClient) {
        this.bookingRepository = bookingRepository;
        this.discountService = discountService;
        this.bookingClient = bookingClient;
    }

    // Crea una reserva en estado PENDING. No se reservan cupos aqui: eso ocurre recien
    // cuando el pago se confirma (fuera del alcance de esta epica)
    public BookingResponse createBooking(String userId, CreateBookingRequest request) {
        validateUserId(userId);
        validatePassengerCount(request.getPassengerCount());

        PackageResponse packageInfo = bookingClient.getPackageById(request.getPackageId());
        validatePackageBookable(packageInfo, request.getPassengerCount());

        BigDecimal baseAmount = packageInfo.getPrice().multiply(BigDecimal.valueOf(request.getPassengerCount()));
        DiscountResult discount = discountService.calculateDiscounts(
                userId, request.getPassengerCount(), baseAmount, LocalDate.now());

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setPackageId(packageInfo.getId());
        booking.setPassengerCount(request.getPassengerCount());
        booking.setBaseAmount(baseAmount);
        booking.setStatus(BookingStatus.PENDING);
        booking.applyDiscount(discount.totalPercentage(), discount.discountAmount(), discount.details());

        Booking saved = bookingRepository.save(booking);
        return toResponse(saved, packageInfo);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id, String userId) {
        Booking booking = findBookingOrThrow(id);
        validateOwnership(booking, userId);
        return toResponse(booking, fetchPackageSafely(booking.getPackageId()));
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByUser(String userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(booking -> toResponse(booking, fetchPackageSafely(booking.getPackageId())))
                .toList();
    }

    // Solo un administrador puede listar todas las reservas
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings(String role) {
        validateRoleIsAdmin(role);
        return bookingRepository.findAll().stream()
                .map(booking -> toResponse(booking, fetchPackageSafely(booking.getPackageId())))
                .toList();
    }

    // Reservas creadas dentro de un rango de fechas, para reportes (llamado por report-service)
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        return bookingRepository.findByCreatedAtBetween(start, end).stream()
                .map(booking -> toResponse(booking, fetchPackageSafely(booking.getPackageId())))
                .toList();
    }

    // Confirma una reserva PENDING tras un pago exitoso (llamado por payment-service).
    // Es la unica accion que efectivamente reserva cupos en package-service: hasta este
    // punto la reserva era solo una intencion, no un compromiso de cupo.
    public BookingResponse confirmBooking(Long bookingId) {
        Booking booking = findBookingOrThrow(bookingId);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BusinessRuleException(
                    "Solo se puede confirmar una reserva en estado PENDING (actual: " + booking.getStatus() + ")");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        // saveAndFlush (no solo save) para que @PreUpdate corra ya, dentro de esta transaccion:
        // si no, updatedAt en la respuesta queda desactualizado hasta el commit
        Booking saved = bookingRepository.saveAndFlush(booking);

        bookingClient.updatePackageBookedSlots(saved.getPackageId(), saved.getPassengerCount());

        return toResponse(saved, fetchPackageSafely(saved.getPackageId()));
    }

    // Cancela una reserva y libera los cupos que tuviera comprometidos
    public BookingResponse cancelBooking(Long bookingId, String userId) {
        Booking booking = findBookingOrThrow(bookingId);
        validateOwnership(booking, userId);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BusinessRuleException("La reserva ya esta cancelada");
        }
        if (booking.getStatus() == BookingStatus.EXPIRED) {
            throw new BusinessRuleException("No se puede cancelar una reserva expirada");
        }

        BookingStatus previousStatus = booking.getStatus();
        booking.setStatus(BookingStatus.CANCELLED);
        // saveAndFlush (no solo save) para que @PreUpdate corra ya, dentro de esta transaccion:
        // si no, updatedAt en la respuesta queda desactualizado hasta el commit
        Booking saved = bookingRepository.saveAndFlush(booking);

        releaseSlotsIfConfirmed(saved, previousStatus);

        return toResponse(saved, fetchPackageSafely(saved.getPackageId()));
    }

    // Corre cada 5 minutos: expira reservas PENDING que llevan mas de 30 minutos sin confirmarse
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void markAsExpired() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(EXPIRATION_MINUTES);
        List<Booking> expired = bookingRepository.findByStatusAndCreatedAtBefore(BookingStatus.PENDING, cutoff);

        for (Booking booking : expired) {
            booking.setStatus(BookingStatus.EXPIRED);
            bookingRepository.save(booking);
            releaseSlotsIfConfirmed(booking, BookingStatus.PENDING);
        }
    }

    // Los cupos solo se habian reservado si la reserva llego a CONFIRMED; si seguia PENDING
    // nunca se tocaron los cupos, asi que no hay nada que liberar
    private void releaseSlotsIfConfirmed(Booking booking, BookingStatus previousStatus) {
        if (previousStatus != BookingStatus.CONFIRMED) {
            return;
        }
        try {
            bookingClient.releaseSlots(booking.getPackageId(), booking.getPassengerCount());
        } catch (PackageServiceUnavailableException ex) {
            log.warn("No se pudieron liberar los cupos del paquete {} para la reserva {}: {}",
                    booking.getPackageId(), booking.getId(), ex.getMessage());
        }
    }

    private Booking findBookingOrThrow(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una reserva con id " + id));
    }

    private void validateUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new BusinessRuleException("El usuario debe estar autenticado");
        }
    }

    private void validatePassengerCount(Integer passengerCount) {
        if (passengerCount == null || passengerCount <= 0) {
            throw new BusinessRuleException("La cantidad de pasajeros debe ser mayor a cero");
        }
    }

    private void validatePackageBookable(PackageResponse packageInfo, int passengerCount) {
        if (packageInfo.getStatus() != PackageStatus.AVAILABLE) {
            throw new BusinessRuleException(
                    "El paquete no esta disponible para reservar (estado: " + packageInfo.getStatus() + ")");
        }
        if (passengerCount > packageInfo.getAvailableSlots()) {
            throw new BusinessRuleException(
                    "No hay suficientes cupos disponibles (" + packageInfo.getAvailableSlots() + " disponibles)");
        }
    }

    private void validateOwnership(Booking booking, String userId) {
        validateUserId(userId);
        if (!isAdmin(userId) && !booking.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("No tiene permisos para acceder a esta reserva");
        }
    }

    // Distinto de isAdmin(userId) (bypass interno de ownership, ver validateOwnership): este
    // valida el rol real de user-service, que el llamador (frontend) envia explicitamente,
    // igual que package-service/report-service/confirmation-service.
    private void validateRoleIsAdmin(String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("Solo un administrador puede listar todas las reservas");
        }
    }

    private boolean isAdmin(String userId) {
        return ADMIN_USER_ID.equalsIgnoreCase(userId);
    }

    // Si package-service no responde, la reserva igual se muestra, solo sin el nombre/destino
    private PackageResponse fetchPackageSafely(Long packageId) {
        try {
            return bookingClient.getPackageById(packageId);
        } catch (RuntimeException ex) {
            log.warn("No se pudo enriquecer la reserva con datos del paquete {}: {}", packageId, ex.getMessage());
            return null;
        }
    }

    private BookingResponse toResponse(Booking booking, PackageResponse packageInfo) {
        return new BookingResponse(
                booking.getId(),
                booking.getUserId(),
                booking.getPackageId(),
                packageInfo != null ? packageInfo.getName() : null,
                packageInfo != null ? packageInfo.getDestination() : null,
                packageInfo != null ? packageInfo.getStartDate() : null,
                packageInfo != null ? packageInfo.getEndDate() : null,
                booking.getPassengerCount(),
                booking.getBaseAmount(),
                booking.getDiscountPercentage(),
                booking.getDiscountAmount(),
                booking.getDiscountDetails(),
                booking.getTotalAmount(),
                booking.getStatus(),
                booking.getCreatedAt(),
                booking.getUpdatedAt());
    }
}
