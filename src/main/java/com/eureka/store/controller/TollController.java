package com.eureka.store.controller;

import com.eureka.store.dto.TollEvent;
import com.eureka.store.gateway.ITollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/toll")
public class TollController {

    private final ITollService iTollService;

    @Autowired
    public TollController(ITollService iTollService) {
        this.iTollService = iTollService;
    }

    @PostMapping("/v1/create/transaction")
    public String postTransaction(@RequestBody TollEvent event){
        iTollService.processAndPublish(event);
        return "Saved + Publish Kafka";
    }
}
