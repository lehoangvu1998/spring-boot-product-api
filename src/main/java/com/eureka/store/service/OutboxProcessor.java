package com.eureka.store.service;
import com.eureka.store.entity.OutBoxEvent;
import com.eureka.store.repository.IOutBoxEventRepository;
import com.eureka.store.repository.ITollRepository;
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OutboxProcessor {

    private final IOutBoxEventRepository outBoxRepo;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public OutboxProcessor(IOutBoxEventRepository outBoxRepo, KafkaTemplate<String, String> kafkaTemplate) {
        this.outBoxRepo = outBoxRepo;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    public void retryFailedEvent (){
        List<OutBoxEvent> events = outBoxRepo.findTop10ByStatusAndNextRetryAtBefore("FAILED", LocalDateTime.now());
        if(!Collections.isEmpty(events)){
            for (OutBoxEvent event : events) {
                try {
                    kafkaTemplate.send("toll-events", event.getPayload());
                    event.setStatus("SENT");
                } catch (Exception e){
                    event.setRetryCount(event.getRetryCount() + 1 );
                    long delay = (long) Math.pow(2, event.getRetryCount()) * 5;
                    event.setNextRetryAt(LocalDateTime.now().plusSeconds(delay));
                }
                outBoxRepo.save(event);
            }
        }
    }
}
