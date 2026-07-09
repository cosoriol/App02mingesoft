package com.travelagency.paymentservice.service;

import com.travelagency.paymentservice.dto.PaymentRequest;
import com.travelagency.paymentservice.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

// Valida unicamente el FORMATO de los datos de la tarjeta: como los pagos son simulados,
// nunca se valida contra un banco o pasarela real
@Service
public class PaymentValidationService {

    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile("\\d{16}");
    private static final Pattern CVV_PATTERN = Pattern.compile("\\d{3}");
    private static final DateTimeFormatter EXPIRATION_FORMAT = DateTimeFormatter.ofPattern("MM/yy");

    public void validateCardFormat(PaymentRequest request) {
        validateCardNumber(request.getCardNumber());
        validateCvv(request.getCvv());
        validateExpirationDate(request.getExpirationDate());
        validateCardHolderName(request.getCardHolderName());
    }

    private void validateCardNumber(String cardNumber) {
        if (cardNumber == null || !CARD_NUMBER_PATTERN.matcher(cardNumber).matches()) {
            throw new BusinessRuleException("El numero de tarjeta debe tener exactamente 16 digitos numericos");
        }
    }

    private void validateCvv(String cvv) {
        if (cvv == null || !CVV_PATTERN.matcher(cvv).matches()) {
            throw new BusinessRuleException("El CVV debe tener exactamente 3 digitos numericos");
        }
    }

    private void validateExpirationDate(String expirationDate) {
        YearMonth expiration;
        try {
            expiration = YearMonth.parse(expirationDate, EXPIRATION_FORMAT);
        } catch (DateTimeException ex) {
            throw new BusinessRuleException("La fecha de expiracion debe tener formato MM/YY");
        }

        if (expiration.isBefore(YearMonth.now())) {
            throw new BusinessRuleException("La tarjeta esta vencida");
        }
    }

    private void validateCardHolderName(String cardHolderName) {
        if (cardHolderName == null || cardHolderName.isBlank()) {
            throw new BusinessRuleException("El nombre del titular de la tarjeta es obligatorio");
        }
    }
}
