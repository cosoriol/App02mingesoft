package com.travelagency.bookingservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// Parametros del motor de descuentos, leidos desde el Config Server (prefijo app.discount)
@Component
@ConfigurationProperties(prefix = "app.discount")
public class DiscountConfig {

    private int groupThreshold = 4;
    private BigDecimal groupPercentage = new BigDecimal("5.0");

    private int frequentThreshold = 3;
    private BigDecimal frequentPercentage = new BigDecimal("10.0");

    private int multiPackageDays = 30;
    private BigDecimal multiPackagePercentage = new BigDecimal("3.0");

    private BigDecimal maxTotalDiscount = new BigDecimal("20.0");

    public int getGroupThreshold() {
        return groupThreshold;
    }

    public void setGroupThreshold(int groupThreshold) {
        this.groupThreshold = groupThreshold;
    }

    public BigDecimal getGroupPercentage() {
        return groupPercentage;
    }

    public void setGroupPercentage(BigDecimal groupPercentage) {
        this.groupPercentage = groupPercentage;
    }

    public int getFrequentThreshold() {
        return frequentThreshold;
    }

    public void setFrequentThreshold(int frequentThreshold) {
        this.frequentThreshold = frequentThreshold;
    }

    public BigDecimal getFrequentPercentage() {
        return frequentPercentage;
    }

    public void setFrequentPercentage(BigDecimal frequentPercentage) {
        this.frequentPercentage = frequentPercentage;
    }

    public int getMultiPackageDays() {
        return multiPackageDays;
    }

    public void setMultiPackageDays(int multiPackageDays) {
        this.multiPackageDays = multiPackageDays;
    }

    public BigDecimal getMultiPackagePercentage() {
        return multiPackagePercentage;
    }

    public void setMultiPackagePercentage(BigDecimal multiPackagePercentage) {
        this.multiPackagePercentage = multiPackagePercentage;
    }

    public BigDecimal getMaxTotalDiscount() {
        return maxTotalDiscount;
    }

    public void setMaxTotalDiscount(BigDecimal maxTotalDiscount) {
        this.maxTotalDiscount = maxTotalDiscount;
    }
}
