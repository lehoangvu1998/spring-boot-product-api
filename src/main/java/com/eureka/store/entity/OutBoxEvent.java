package com.eureka.store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table (name = "outbox_event")
@Setter
@Getter
public class OutBoxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String eventType;
    private String payload;
    private String status;
    private int retryCount;
    private LocalDateTime nextRetryAt;
    private LocalDateTime createdAt;

    public OutBoxEvent() {
    }

    public OutBoxEvent(String eventType, String payload, String status, int retryCount, LocalDateTime nextRetryAt, LocalDateTime createdAt) {
        this.eventType = eventType;
        this.payload = payload;
        this.status = status;
        this.retryCount = retryCount;
        this.nextRetryAt = nextRetryAt;
        this.createdAt = createdAt;
    }
}
