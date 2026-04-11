package com.eureka.store.service;
import com.eureka.store.entity.OutBoxEvent;
import com.eureka.store.repository.IOutBoxEventRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OutboxProcessor {

    private final IOutBoxEventRepository outBoxRepo;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_PROCESSING = "PROCESSING";
    private static final String STATUS_SENT = "SENT";
    private static final String STATUS_FAILED = "FAILED";
    private static final String STATUS_DEAD = "DEAD";

    public OutboxProcessor(IOutBoxEventRepository outBoxRepo,
                           KafkaTemplate<String, String> kafkaTemplate) {
        this.outBoxRepo = outBoxRepo;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 3000)
    public void pollAndProcess() {
        List<OutBoxEvent> events = fetchAndLockBatch(10);

        if (events.isEmpty()) return;

        for (OutBoxEvent event : events) {
            processEvent(event);
        }
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
            handleFailure(event, ex);
        }

        outBoxRepo.save(event);
    }

    private void handleFailure(OutBoxEvent event, Exception ex) {
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
}
