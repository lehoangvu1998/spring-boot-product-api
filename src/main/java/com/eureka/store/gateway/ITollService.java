package com.eureka.store.gateway;

import com.eureka.store.dto.TollEvent;

public interface ITollService {
    void processAndPublish (TollEvent event);
}
