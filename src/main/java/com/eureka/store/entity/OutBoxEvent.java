package com.eureka.store.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table (name = "outbox_event", uniqueConstraints = {
@UniqueConstraint(columnNames = "eventId")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OutBoxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String eventType;
    private String eventId;
    private String payload;
    private String status;
    private int retryCount;
    private LocalDateTime nextRetryAt;
    private LocalDateTime createdAt;
    private LocalDateTime lockedAt;
}
