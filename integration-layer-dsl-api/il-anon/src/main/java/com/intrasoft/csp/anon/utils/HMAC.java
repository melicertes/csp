package com.intrasoft.csp.anon.utils;

import com.intrasoft.csp.anon.model.SecretKey;
import com.intrasoft.csp.anon.repository.SecretKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.UUID;

@Component
public class HMAC {

    @Autowired
    SecretKeyRepository secretKeyRepository;

    SecretKey key;

    @PostConstruct
    public void init(){
        key = secretKeyRepository.save(new SecretKey(UUID.randomUUID().toString(), new Date()));
    }

    @Scheduled(fixedDelayString="${key.update}")
    public void updateKey(){
        key = secretKeyRepository.save(new SecretKey(UUID.randomUUID().toString(), new Date()));
    }

    public SecretKey getKey() {
        return key;
    }
}
