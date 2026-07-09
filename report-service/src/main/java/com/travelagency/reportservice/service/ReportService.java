package com.travelagency.reportservice.service;

import com.travelagency.reportservice.client.BookingServiceClient;
import com.travelagency.reportservice.client.PackageServiceClient;
import com.travelagency.reportservice.client.PaymentServiceClient;
import com.travelagency.reportservice.dto.BookingResponse;
import com.travelagency.reportservice.dto.BookingStatus;
import com.travelagency.reportservice.dto.PackageRankingItem;
import com.travelagency.reportservice.dto.PackageRankingResponse;
import com.travelagency.reportservice.dto.PackageRankingSummary;
import com.travelagency.reportservice.dto.PackageResponse;
import com.travelagency.reportservice.dto.PaymentResponse;
import com.travelagency.reportservice.dto.SalesReportItem;
import com.travelagency.reportservice.dto.SalesReportResponse;
import com.travelagency.reportservice.dto.SalesReportSummary;
import com.travelagency.reportservice.exception.BusinessRuleException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

// Genera reportes de ventas y ranking de paquetes bajo demanda, combinando datos en vivo
// de booking-service, payment-service y package-service (sin cacheo)
@Service
@Transactional(readOnly = true)
public class ReportService {

    private static final Set<BookingStatus> RANKING_STATUSES = EnumSet.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);

    private final BookingServiceClient bookingClient;
    private final PaymentServiceClient paymentClient;
    private final PackageServiceClient packageClient;

    public ReportService(BookingServiceClient bookingClient, PaymentServiceClient paymentClient,
                          PackageServiceClient packageClient) {
        this.bookingClient = bookingClient;
        this.paymentClient = paymentClient;
        this.packageClient = packageClient;
    }

    public SalesReportResponse generateSalesReport(LocalDate startDate, LocalDate endDate, boolean includeCancelled) {
        validateDateRange(startDate, endDate);

        List<BookingResponse> bookings = bookingClient.getBookingsByDateRange(startDate, endDate).stream()
                .filter(b -> includeCancelled || b.getStatus() != BookingStatus.CANCELLED)
                .toList();

        Map<Long, PaymentResponse> paymentsByBookingId = fetchPaymentsByBookingId(bookings);

        List<SalesReportItem> items = bookings.stream()
                .map(booking -> toSalesItem(booking, paymentsByBookingId.get(booking.getId())))
                .sorted(Comparator.comparing(SalesReportItem::getFecha).reversed())
                .toList();

        SalesReportSummary summary = buildSalesSummary(bookings, paymentsByBookingId);

        return new SalesReportResponse(items, summary);
    }

    public PackageRankingResponse generatePackageRanking(LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);

        List<BookingResponse> bookings = bookingClient.getBookingsByDateRange(startDate, endDate).stream()
                .filter(b -> RANKING_STATUSES.contains(b.getStatus()))
                .toList();

        Map<Long, PaymentResponse> paymentsByBookingId = fetchPaymentsByBookingId(bookings);

        Map<Long, List<BookingResponse>> byPackage = bookings.stream()
                .collect(Collectors.groupingBy(BookingResponse::getPackageId, LinkedHashMap::new, Collectors.toList()));

        List<PackageRankingItem> ranked = byPackage.values().stream()
                .map(group -> toRankingItem(group, paymentsByBookingId))
                .sorted(Comparator
                        .comparingLong(PackageRankingItem::getBookingCount).reversed()
                        .thenComparing(Comparator.comparingLong(PackageRankingItem::getTotalPassengers).reversed())
                        .thenComparing(Comparator.comparing(PackageRankingItem::getTotalAmount).reversed()))
                .toList();

        List<PackageRankingItem> withRank = assignRanks(ranked);

        PackageRankingSummary summary = buildRankingSummary(withRank);

        return new PackageRankingResponse(withRank, summary);
    }

    private Map<Long, PaymentResponse> fetchPaymentsByBookingId(List<BookingResponse> bookings) {
        List<Long> bookingIds = bookings.stream().map(BookingResponse::getId).toList();
        return paymentClient.getPaymentsByBookingIds(bookingIds).stream()
                .collect(Collectors.toMap(PaymentResponse::getBookingId, payment -> payment));
    }

    private SalesReportItem toSalesItem(BookingResponse booking, PaymentResponse payment) {
        return new SalesReportItem(
                booking.getCreatedAt().toLocalDate(),
                // No existe un servicio de usuarios/clientes en el sistema (no hay integracion
                // real con Keycloak): el unico identificador disponible es el userId de la
                // reserva. clientEmail queda null porque no hay ninguna fuente que lo resuelva.
                booking.getUserId(),
                null,
                booking.getPackageName(),
                booking.getDestination(),
                booking.getPassengerCount(),
                booking.getBaseAmount(),
                booking.getDiscountPercentage(),
                booking.getDiscountAmount(),
                booking.getTotalAmount(),
                payment != null ? payment.getAmount() : null,
                readableStatus(booking.getStatus()));
    }

    private PackageRankingItem toRankingItem(List<BookingResponse> group, Map<Long, PaymentResponse> paymentsByBookingId) {
        BookingResponse first = group.get(0);

        long totalPassengers = group.stream().mapToLong(BookingResponse::getPassengerCount).sum();
        BigDecimal totalAmount = group.stream()
                .map(BookingResponse::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCollected = group.stream()
                .map(b -> paymentsByBookingId.get(b.getId()))
                .filter(Objects::nonNull)
                .map(PaymentResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PackageResponse packageInfo = packageClient.getPackageById(first.getPackageId());

        // El rank definitivo se asigna despues de ordenar (ver assignRanks); 0 es un valor temporal
        return new PackageRankingItem(
                0,
                first.getPackageName(),
                first.getDestination(),
                group.size(),
                totalPassengers,
                totalAmount,
                totalCollected,
                packageInfo != null ? packageInfo.getPrice() : null);
    }

    private List<PackageRankingItem> assignRanks(List<PackageRankingItem> ordered) {
        List<PackageRankingItem> result = new ArrayList<>(ordered.size());
        int rank = 1;
        for (PackageRankingItem item : ordered) {
            result.add(new PackageRankingItem(
                    rank++,
                    item.getPackageName(),
                    item.getDestination(),
                    item.getBookingCount(),
                    item.getTotalPassengers(),
                    item.getTotalAmount(),
                    item.getTotalCollected(),
                    item.getUnitPrice()));
        }
        return result;
    }

    private SalesReportSummary buildSalesSummary(List<BookingResponse> bookings, Map<Long, PaymentResponse> paymentsByBookingId) {
        long totalPassengers = bookings.stream().mapToLong(BookingResponse::getPassengerCount).sum();
        BigDecimal totalSalesAmount = bookings.stream()
                .map(BookingResponse::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCollectedAmount = bookings.stream()
                .map(b -> paymentsByBookingId.get(b.getId()))
                .filter(Objects::nonNull)
                .map(PaymentResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, Long> bookingsByStatus = bookings.stream()
                .collect(Collectors.groupingBy(b -> readableStatus(b.getStatus()), LinkedHashMap::new, Collectors.counting()));

        return new SalesReportSummary(bookings.size(), totalPassengers, totalSalesAmount, totalCollectedAmount, bookingsByStatus);
    }

    private PackageRankingSummary buildRankingSummary(List<PackageRankingItem> items) {
        long totalBookings = items.stream().mapToLong(PackageRankingItem::getBookingCount).sum();
        long totalPassengers = items.stream().mapToLong(PackageRankingItem::getTotalPassengers).sum();
        BigDecimal totalAmount = items.stream()
                .map(PackageRankingItem::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PackageRankingSummary(items.size(), totalBookings, totalPassengers, totalAmount);
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new BusinessRuleException("La fecha de inicio debe ser anterior o igual a la fecha de termino");
        }
    }

    // Traduce el estado tecnico de la reserva a un texto legible para los reportes
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
}
