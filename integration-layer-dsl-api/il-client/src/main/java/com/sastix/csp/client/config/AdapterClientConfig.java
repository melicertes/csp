package com.sastix.csp.client.config;

import com.sastix.csp.client.AdapterClient;
import com.sastix.csp.client.impl.AdapterClientImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by iskitsas on 4/6/17.
 */
@Configuration
public class AdapterClientConfig {
    @Value("${adapter.server.protocol}")
    private String protocol;

    @Value("${adapter.server.host}")
    private String host;

    @Value("${adapter.server.port}")
    private String port;

    @Bean(name = "adapterClient")
    public AdapterClient getAdapterClient(){
        AdapterClient adapterClient = new AdapterClientImpl();
        adapterClient.setProtocolHostPort(protocol,host,port);
        return adapterClient;
    }
}
