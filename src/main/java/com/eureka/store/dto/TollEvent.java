package com.eureka.store.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class TollEvent {
    private String transactionId;
    private String licenseNumber;
    private BigDecimal amount;

    public TollEvent() {
    }

    public TollEvent(String transactionId, String licenseNumber, BigDecimal amount) {
        this.transactionId = transactionId;
        this.licenseNumber = licenseNumber;
        this.amount = amount;
    }
}
