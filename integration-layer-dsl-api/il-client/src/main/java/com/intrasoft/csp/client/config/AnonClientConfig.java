package com.intrasoft.csp.client.config;

import com.intrasoft.csp.client.AnonClient;
import com.intrasoft.csp.client.impl.AnonClientImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by chris on 14/7/2017.
 */
@Configuration
public class AnonClientConfig {
    @Value("${anon.protocol}")
    private String protocol;

    @Value("${anon.host}")
    private String host;

    @Value("${anon.port}")
    private String port;

    @Bean(name = "anonClient")
    public AnonClient getAnonClient(){
        AnonClient client = new AnonClientImpl();
        client.setProtocolHostPort(protocol,host,port);
        return client;
    }
}
