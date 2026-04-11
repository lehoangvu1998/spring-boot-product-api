package com.eureka.store.service;

import com.eureka.store.annotation.LogEvent; // Annotation chúng ta đã tạo
import com.eureka.store.annotation.SendEmailOnFailure;
import com.eureka.store.dto.TollEvent;
import com.eureka.store.entity.OutBoxEvent;
import com.eureka.store.entity.Toll;
import com.eureka.store.gateway.ITollService;
import com.eureka.store.repository.IOutBoxEventRepository;
import com.eureka.store.repository.ITollRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TollServiceImpl implements ITollService {

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
    private static final String STATUS_PENDING = "PENDING";

    public TollServiceImpl(IOutBoxEventRepository outBoxRepo,
                           ITollRepository tollRepo,
                           KafkaTemplate<String, String> kafkaTemplate,
                           ObjectMapper objectMapper,
                           OutBoxService outBoxService) {
        this.outBoxRepo = outBoxRepo;
        this.tollRepo = tollRepo;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.outBoxService = outBoxService;
    }

    @Override
    @Transactional
    @LogEvent("PROCESS_TOLL_TRANSACTION") // AOP sẽ lo việc Log START, SUCCESS, DURATION và ERROR
    @SendEmailOnFailure
    public void processAndPublish(TollEvent event) {
        try {
            // Step 1: Save business data
            tollRepo.save(convertData(event));
            // Step 2: Prepare Outbox entry
            String payload = objectMapper.writeValueAsString(event);
            String eventId = UUID.randomUUID().toString();

            OutBoxEvent outbox = createOutboxEntry(eventId, payload);
            OutBoxEvent saved = outBoxRepo.save(outbox);
            Long outboxId = saved.getId();

            // Step 3: Register Kafka sync after DB commit
            registerKafkaSynchronization(outboxId, eventId, payload);

        } catch (JsonProcessingException ex) {
            // Vẫn throw Exception để Aspect bắt được và log ERROR, đồng thời @Transactional sẽ Rollback
            throw new RuntimeException("Failed to serialize toll event", ex);
        } catch (Exception ex) {
            throw new RuntimeException("Toll processing failed: " + ex.getMessage(), ex);
        }
    }

    private OutBoxEvent createOutboxEntry(String eventId, String payload) {
        OutBoxEvent outbox = new OutBoxEvent();
        outbox.setEventId(eventId);
        outbox.setPayload(payload);
        outbox.setEventType(EVENT_TYPE_TOLL);
        outbox.setStatus(STATUS_PENDING);
        return outbox;
    }

    private void registerKafkaSynchronization(Long outboxId, String eventId, String payload) {
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
    }

    private Toll convertData(TollEvent input) {
        if (input == null) throw new IllegalArgumentException("TollEvent cannot be null");

        return Toll.builder()
                .transactionId(input.getTransactionId())
                .licenseNumber(input.getLicenseNumber())
                .amount(input.getAmount())
                .createdBy(CREATED_BY)
                .createdDate(LocalDateTime.now())
                .build();
    }
}