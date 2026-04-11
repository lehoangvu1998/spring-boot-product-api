package com.eureka.store.service;

import com.eureka.store.entity.OutBoxEvent;
import com.eureka.store.repository.IOutBoxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OutBoxService {

    private static final Logger log = LoggerFactory.getLogger(OutBoxService.class);

    private static final String STATUS_SENT = "SENT";
    private static final String STATUS_FAILED = "FAILED";
    private static final String STATUS_PROCESSING = "PROCESSING";;
    private static final String STATUS_DEAD = "DEAD";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final IOutBoxEventRepository outBoxRepo;
    private final IOutBoxEventRepository outBoxEventRepository;

    public OutBoxService(KafkaTemplate<String, String> kafkaTemplate, IOutBoxEventRepository outBoxRepo, IOutBoxEventRepository outBoxEventRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.outBoxRepo = outBoxRepo;
        this.outBoxEventRepository = outBoxEventRepository;
    }

    @Transactional
    public List<OutBoxEvent> fetchAndLockBatch(int limit) {
        List<OutBoxEvent> events = outBoxRepo.lockBatchForProcessing(limit);

        for (OutBoxEvent e : events) {
            e.setStatus(STATUS_PROCESSING);
            e.setLockedAt(LocalDateTime.now());
        }

        return outBoxRepo.saveAll(events);
    }

    @Transactional
    public void processEvent(OutBoxEvent event) {
        try {
            kafkaTemplate.send(
                    "toll-events",
                    event.getEventId(),
                    event.getPayload()
            ).get();

            event.setStatus(STATUS_SENT);

        } catch (Exception ex) {
            handleFailure(event);
        }

        outBoxRepo.save(event);
    }

    private void handleFailure(OutBoxEvent event) {
        int retry = event.getRetryCount() + 1;
        event.setRetryCount(retry);

        long delay = (long) Math.pow(2, retry) * 5;
        event.setNextRetryAt(LocalDateTime.now().plusSeconds(delay));

        if (retry >= 10) {
            event.setStatus(STATUS_DEAD);
        } else {
            event.setStatus(STATUS_FAILED);
        }
    }

    @Transactional
    public void updateOutboxStatus(Long outboxId, String status, Throwable ex) {
        var outbox = outBoxEventRepository.findById(outboxId)
                .orElseThrow(() -> new RuntimeException("Outbox not found with id: " + outboxId));

        if (STATUS_FAILED.equals(status)) {
            markFailed(outbox);
            log.error("Kafka send failed for outboxId={}", outboxId, ex);
        } else {
            outbox.setStatus(STATUS_SENT);
        }

        outBoxEventRepository.save(outbox);
    }

    private void markFailed(OutBoxEvent outbox) {
        outbox.setStatus(STATUS_FAILED);
        outbox.setRetryCount(outbox.getRetryCount() + 1);
        long delay = (long) Math.pow(2, outbox.getRetryCount()) * 5;
        outbox.setNextRetryAt(LocalDateTime.now().plusSeconds(delay));
    }
}
