package com.eureka.store.service;

import com.eureka.store.common.BaseApiService;
import com.eureka.store.dto.VehicleDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProductService extends BaseApiService<VehicleDTO> {

    protected ProductService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    protected String getBaseUrl() {
        return "";
    }

    public VehicleDTO findById(Long id){
        return getOne("/" + id, VehicleDTO.class);
    }
}
