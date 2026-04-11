package com.eureka.store.service;

import com.eureka.store.dto.TollEvent;
import com.eureka.store.entity.OutBoxEvent;
import com.eureka.store.entity.Toll;
import com.eureka.store.gateway.ITollService;
import com.eureka.store.repository.IOutBoxEventRepository;
import com.eureka.store.repository.ITollRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class TollServiceImpl implements ITollService {

    private final IOutBoxEventRepository outBoxRepo;

    private final ITollRepository tollRepo;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Autowired
    public TollServiceImpl(IOutBoxEventRepository outBoxRepo, ITollRepository tollRepo, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.outBoxRepo = outBoxRepo;
        this.tollRepo = tollRepo;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void processAndPublish(TollEvent event) {
        try {
            // step 1 : save data to database
            tollRepo.save(convertData(event));

            // step 2 : back-up data
            String payload = objectMapper.writeValueAsString(event);
            OutBoxEvent outbox = new OutBoxEvent();
            outbox.setPayload(payload);
            outbox.setEventType("TOLL_EVENT");
            outBoxRepo.save(outbox);

            // step 3 : publish Kafka

            publishToKafka(outbox, payload);

        } catch (Exception ex){
            throw new RuntimeException ("Process failed",  ex);
        }
    }

    private void publishToKafka(OutBoxEvent outbox, String payload) {
        kafkaTemplate.send("toll-events", payload)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        outbox.setStatus("SENT");
                    } else {
                        markFailed(outbox);
                    }
                    outBoxRepo.save(outbox);
                });
    }

    private void markFailed (OutBoxEvent output){
        output.setStatus("FAILED");
        output.setRetryCount(output.getRetryCount() + 1 );
        long delay = (long) Math.pow(2, output.getRetryCount()) * 5;
        output.setNextRetryAt(LocalDateTime.now().plusSeconds(delay));
    }

    private Toll convertData (TollEvent input) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return Toll.builder()
                .transactionId(input.getTransactionId())
                .LicenseNumber(input.getLicenseNumber())
                .amount(input.getAmount())
                .createdBy("SYSTEM")
                .createdDate(now.toLocalDateTime())
                .build();
    }
}
