package com.intrasoft.csp.misp.config;

import com.intrasoft.csp.misp.service.MispTcSyncService;
import com.intrasoft.csp.misp.service.impl.MispTcSyncServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MispTcSyncServiceConfig {

    @Bean(name = "mispTcSyncService")
    public MispTcSyncService mispTcSyncService(){
        return new MispTcSyncServiceImpl();
    }
}
