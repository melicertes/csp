package com.sastix.csp.client.config;

import com.sastix.csp.client.TrustCirclesClient;
import com.sastix.csp.client.impl.TrustCirclesClientImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by iskitsas on 4/8/17.
 */
@Configuration
public class TrustCirclesClientConfig {
    @Value("${tc.protocol}")
    private String protocol;

    @Value("${tc.host}")
    private String host;

    @Value("${tc.port}")
    private String port;

    @Bean(name = "trustCirclesClient")
    public TrustCirclesClient getTrustCirclesClient(){
        TrustCirclesClient client = new TrustCirclesClientImpl();
        client.setProtocolHostPort(protocol,host,port);
        return client;
    }
}
