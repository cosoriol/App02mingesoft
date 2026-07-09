package com.travelagency.confirmationservice.dto;

import java.time.LocalDateTime;

// Comprobante de una reserva confirmada: todos los datos de BookingDetailResponse
// mas el numero de comprobante y la fecha de emision
public class BookingVoucherResponse extends BookingDetailResponse {

    private final String voucherNumber;
    private final LocalDateTime issuedDate;

    public BookingVoucherResponse(BookingDetailResponse detail, String voucherNumber, LocalDateTime issuedDate) {
        super(detail.getId(), detail.getUserId(), detail.getPackageId(), detail.getPackageName(),
                detail.getDestination(), detail.getPassengerCount(), detail.getStartDate(), detail.getEndDate(),
                detail.getBaseAmount(), detail.getDiscountPercentage(), detail.getDiscountAmount(),
                detail.getDiscountDetails(), detail.getTotalAmount(), detail.getPaymentInfo(), detail.getStatus(),
                detail.getCreatedAt(), detail.getUpdatedAt());
        this.voucherNumber = voucherNumber;
        this.issuedDate = issuedDate;
    }

    public String getVoucherNumber() {
        return voucherNumber;
    }

    public LocalDateTime getIssuedDate() {
        return issuedDate;
    }
}
