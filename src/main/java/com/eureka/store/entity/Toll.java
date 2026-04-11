package com.eureka.store.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table (name = "Toll")
@Setter
@Getter
@Builder
public class Toll {
    @Id
    private String transactionId;
    @Column (name = "License_Number")
    private String LicenseNumber;
    @Column (name = "amount")
    private BigDecimal amount;
    @Column (name = "created_Date")
    private LocalDateTime createdDate;
    @Column (name = "created_By")
    private String createdBy;

    public Toll() {
    }

    public Toll(String transactionId, String licenseNumber, BigDecimal amount, LocalDateTime createdDate, String createdBy) {
        this.transactionId = transactionId;
        LicenseNumber = licenseNumber;
        this.amount = amount;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
    }
}
