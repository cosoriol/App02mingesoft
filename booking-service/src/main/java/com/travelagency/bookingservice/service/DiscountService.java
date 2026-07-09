package com.travelagency.bookingservice.service;

import com.travelagency.bookingservice.config.DiscountConfig;
import com.travelagency.bookingservice.entity.BookingStatus;
import com.travelagency.bookingservice.entity.Promotion;
import com.travelagency.bookingservice.repository.BookingRepository;
import com.travelagency.bookingservice.repository.PromotionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

// Calcula los descuentos acumulables aplicables a una reserva nueva
@Service
public class DiscountService {

    // Estados que cuentan como actividad real del usuario (se excluyen CANCELLED/EXPIRED)
    private static final Set<BookingStatus> ACTIVE_STATUSES = EnumSet.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);

    private final DiscountConfig config;
    private final BookingRepository bookingRepository;
    private final PromotionRepository promotionRepository;

    public DiscountService(DiscountConfig config, BookingRepository bookingRepository,
                            PromotionRepository promotionRepository) {
        this.config = config;
        this.bookingRepository = bookingRepository;
        this.promotionRepository = promotionRepository;
    }

    public DiscountResult calculateDiscounts(String userId, int passengerCount, BigDecimal baseAmount, LocalDate now) {
        BigDecimal totalPercentage = BigDecimal.ZERO;
        List<String> details = new ArrayList<>();

        // 1. Descuento por grupo
        if (passengerCount >= config.getGroupThreshold()) {
            totalPercentage = totalPercentage.add(config.getGroupPercentage());
            details.add("Descuento por grupo: " + formatPercentage(config.getGroupPercentage()) + "%");
        }

        // 2. Cliente frecuente: reservas ya confirmadas previamente
        long confirmedBookings = bookingRepository.countByUserIdAndStatus(userId, BookingStatus.CONFIRMED);
        if (confirmedBookings >= config.getFrequentThreshold()) {
            totalPercentage = totalPercentage.add(config.getFrequentPercentage());
            details.add("Cliente frecuente: " + formatPercentage(config.getFrequentPercentage()) + "%");
        }

        // 3. Multi-paquete: al menos una reserva activa en los ultimos N dias
        LocalDateTime since = now.minusDays(config.getMultiPackageDays()).atStartOfDay();
        long recentBookings = bookingRepository.countByUserIdAndCreatedAtAfterAndStatusIn(userId, since, ACTIVE_STATUSES);
        if (recentBookings >= 1) {
            totalPercentage = totalPercentage.add(config.getMultiPackagePercentage());
            details.add("Multi-paquete: " + formatPercentage(config.getMultiPackagePercentage()) + "%");
        }

        // 4. Promociones vigentes a la fecha
        List<Promotion> activePromotions = promotionRepository
                .findByActiveAndStartDateLessThanEqualAndEndDateGreaterThanEqual(true, now, now);
        for (Promotion promotion : activePromotions) {
            totalPercentage = totalPercentage.add(promotion.getDiscountPercentage());
            details.add(promotion.getName() + ": " + formatPercentage(promotion.getDiscountPercentage()) + "%");
        }

        // 5. Tope maximo acumulado
        boolean capped = totalPercentage.compareTo(config.getMaxTotalDiscount()) > 0;
        BigDecimal appliedPercentage = capped ? config.getMaxTotalDiscount() : totalPercentage;

        // 6. Monto de descuento sobre el monto base
        BigDecimal discountAmount = baseAmount
                .multiply(appliedPercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // 7. Detalle legible del calculo
        String detailText = buildDetailText(details, appliedPercentage, capped);

        return new DiscountResult(appliedPercentage, discountAmount, detailText);
    }

    private String buildDetailText(List<String> details, BigDecimal appliedPercentage, boolean capped) {
        StringBuilder text = new StringBuilder();
        if (!details.isEmpty()) {
            text.append(String.join(" | ", details)).append(" | ");
        }
        text.append("Total: ").append(formatPercentage(appliedPercentage)).append("%");
        if (capped) {
            text.append(" (tope: ").append(formatPercentage(config.getMaxTotalDiscount())).append("%)");
        }
        return text.toString();
    }

    private String formatPercentage(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }
}
