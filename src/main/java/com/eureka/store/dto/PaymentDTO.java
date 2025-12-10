package com.eureka.store.dto;

import com.eureka.store.entity.Person;
import com.eureka.store.utils.PaymentStatus;
import com.eureka.store.utils.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {
    private Long paymentId;
    private PersonDTO person;
    private Long orderId;

    // ==== PAYMENT METHOD INFO ====
    private String methodName;
    private PaymentType methodType;
    private boolean methodActive;
    private PaymentStatus status;
    private String transactionId;
    private Double amount;

    // ==== OPTIONAL USER PAYMENT DETAILS ====
    private String payerName;
    private String cardNumberMasked;
    private String paypalEmail;

    private Timestamp createdDate;
    private String createdBy;
    private String modifiedBy;
    private Timestamp dateModified;
}
