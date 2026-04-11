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
import java.util.UUID;

@Service
public class TollServiceImpl implements ITollService {

    private static final Logger log = LoggerFactory.getLogger(TollServiceImpl.class);

    private final IOutBoxEventRepository outBoxRepo;
    private final ITollRepository tollRepo;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final OutBoxService outBoxService;

    private final String tollEventsTopic = "toll-events";

    private static final String STATUS_SENT = "SENT";
    private static final String STATUS_FAILED = "FAILED";
    private static final String EVENT_TYPE_TOLL = "TOLL_CREATED";
    private static final String CREATED_BY = "SYSTEM";

    public TollServiceImpl(IOutBoxEventRepository outBoxRepo,
                           ITollRepository tollRepo,
                           KafkaTemplate<String, String> kafkaTemplate,
                           ObjectMapper objectMapper, OutBoxService outBoxService) {
        this.outBoxRepo = outBoxRepo;
        this.tollRepo = tollRepo;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.outBoxService = outBoxService;
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

            OutBoxEvent saved = outBoxRepo.save(outbox);
            Long outboxId = saved.getId();

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            kafkaTemplate.send(tollEventsTopic, eventId, payload)
                                    .whenComplete((result, ex) -> outBoxService.updateOutboxStatus(
                                    outboxId,
                                    ex == null ? STATUS_SENT : STATUS_FAILED,
                                    ex
                            ));
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

    private Toll convertData(TollEvent input) {

        if (input == null) {
            throw new IllegalArgumentException("TollEvent cannot be null");
        }
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