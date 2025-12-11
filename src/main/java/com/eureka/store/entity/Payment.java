package com.eureka.store.entity;

import com.eureka.store.utils.PaymentStatus;
import com.eureka.store.utils.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    // ==== RELATIONSHIP ====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;

    // ==== ORDER INFO ====
    @Column(nullable = false)
    private Long orderId;

    // ==== PAYMENT METHOD INFO ====
    @Column(nullable = false)
    private String methodName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType methodType;

    @Column(nullable = false)
    private boolean methodActive = true;

    // ==== PAYMENT TRANSACTION INFO ====
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private String transactionId;

    private Double amount;

    // ==== OPTIONAL USER PAYMENT DETAILS ====
    private String payerName;
    private String cardNumberMasked;
    private String paypalEmail;

    // ==== TIMESTAMP ====
    private Timestamp createdDate;
    private String createdBy;
    private String modifiedBy;
    private Timestamp dateModified;
}
