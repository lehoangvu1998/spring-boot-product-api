package com.eureka.store.service;

import com.eureka.store.dto.TollEvent;
import com.eureka.store.entity.OutBoxEvent;
import com.eureka.store.entity.Toll;
import com.eureka.store.gateway.ITollService;
import com.eureka.store.repository.IOutBoxEventRepository;
import com.eureka.store.repository.ITollRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TollServiceImpl implements ITollService {

    private static final Logger log = LoggerFactory.getLogger(TollServiceImpl.class);

    private final IOutBoxEventRepository outBoxRepo;
    private final ITollRepository tollRepo;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private final String tollEventsTopic = "toll-events";

    private static final String STATUS_SENT = "SENT";
    private static final String STATUS_FAILED = "FAILED";
    private static final String EVENT_TYPE_TOLL = "TOLL_CREATED";
    private static final String CREATED_BY = "SYSTEM";

    public TollServiceImpl(IOutBoxEventRepository outBoxRepo,
                           ITollRepository tollRepo,
                           KafkaTemplate<String, String> kafkaTemplate,
                           ObjectMapper objectMapper) {
        this.outBoxRepo = outBoxRepo;
        this.tollRepo = tollRepo;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void processAndPublish(TollEvent event) {
        try {
            //  Step 1: Save business data
            tollRepo.save(convertData(event));

            // Step 2: Save outbox
            String payload = objectMapper.writeValueAsString(event);

            String eventId = UUID.randomUUID().toString();

            OutBoxEvent outbox = new OutBoxEvent();
            outbox.setEventId(eventId);
            outbox.setPayload(payload);
            outbox.setEventType(EVENT_TYPE_TOLL);
            outbox.setStatus("PENDING");

            OutBoxEvent saved = outBoxRepo.saveAndFlush(outbox);
            Long outboxId = saved.getId();

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            kafkaTemplate.send(tollEventsTopic, eventId, payload)
                                    .whenComplete((result, ex) -> {
                                        updateOutboxStatus(
                                                outboxId,
                                                ex == null ? STATUS_SENT : STATUS_FAILED,
                                                ex
                                        );
                                    });
                        }
                    }
            );

        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize event: {}", event, ex);
            throw new RuntimeException("Failed to process toll event", ex);
        } catch (Exception ex) {
            log.error("Unexpected error while processing toll event", ex);
            throw new RuntimeException("Processing failed", ex);
        }
    }

    @Transactional
    public void updateOutboxStatus(Long outboxId, String status, Throwable ex) {
        OutBoxEvent outbox = outBoxRepo.findById(outboxId).orElse(null);

        if (outbox == null) {
            log.error("Outbox not found with id: {}", outboxId);
            return;
        }

        if (STATUS_FAILED.equals(status)) {
            markFailed(outbox);
            log.error("Kafka send failed for outboxId={}", outboxId, ex);
        } else {
            outbox.setStatus(STATUS_SENT);
        }

        outBoxRepo.save(outbox);
    }

    private void markFailed(OutBoxEvent outbox) {
        outbox.setStatus(STATUS_FAILED);
        outbox.setRetryCount(outbox.getRetryCount() + 1);

        long delay = (long) Math.pow(2, outbox.getRetryCount()) * 5;
        outbox.setNextRetryAt(LocalDateTime.now().plusSeconds(delay));
    }

    private Toll convertData(TollEvent input) {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        return Toll.builder()
                .transactionId(input.getTransactionId())
                .licenseNumber(input.getLicenseNumber())
                .amount(input.getAmount())
                .createdBy(CREATED_BY)
                .createdDate(now.toLocalDateTime())
                .build();
    }
}