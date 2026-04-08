package com.eureka.store.common;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public abstract class BaseApiService<T> {

    protected abstract String getBaseUrl();
    private final RestTemplate restTemplate;

    protected BaseApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    protected T getOne(String path, Class<T> responseType){
        String fullUrl = getBaseUrl() + path;
        HttpHeaders header = new HttpHeaders();
        header.setBearerAuth("SECRET_TOKEN");
        HttpEntity<Void> entity = new HttpEntity<>(header);
        return restTemplate.exchange(fullUrl, HttpMethod.GET, entity, responseType).getBody();
    }

}
