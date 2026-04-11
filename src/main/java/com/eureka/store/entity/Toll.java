package com.eureka.store.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table (name = "Toll")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Toll {
    @Id
    private String transactionId;
    @Column (name = "License_Number")
    private String licenseNumber;
    @Column (name = "amount")
    private BigDecimal amount;
    @Column (name = "created_Date")
    private LocalDateTime createdDate;
    @Column (name = "created_By")
    private String createdBy;

}
