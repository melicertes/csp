package com.intrasoft.csp.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Component
public class SecretKey {

    private String key;

    @PostConstruct
    public void init(){
        setKey(UUID.randomUUID().toString());
    }

    @Scheduled(fixedDelayString="${key.update}")
    public void updateKey(){
        setKey(UUID.randomUUID().toString());
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
