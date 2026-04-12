package com.eureka.store.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table (name = "outbox_event", uniqueConstraints = {
@UniqueConstraint(columnNames = "eventId")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    private LocalDateTime createdDate;
    private LocalDateTime lockedAt;
    @Version
    private Long version;
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }

}
