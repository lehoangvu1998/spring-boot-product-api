package com.eureka.store.service;
import com.eureka.store.entity.OutBoxEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.*;

import java.util.List;

@Service
public class OutboxProcessor {

    private final OutBoxService outBoxService;

    public OutboxProcessor(OutBoxService outBoxService) {
        this.outBoxService = outBoxService;
    }

    @Scheduled(fixedDelay = 3000 )
    public void pollAndProcess() {
        List<OutBoxEvent> events = outBoxService.fetchAndLockBatch(10);
        if (events.isEmpty()) return;
        events.parallelStream().forEach( event -> {
            try{
                outBoxService.processEvent(event);
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        });
    }


}
