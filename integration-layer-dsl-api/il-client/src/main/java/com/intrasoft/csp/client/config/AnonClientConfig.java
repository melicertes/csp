package com.intrasoft.csp.client.config;

import com.intrasoft.csp.client.AnonClient;
import com.intrasoft.csp.client.impl.AnonClientImpl;
import com.intrasoft.csp.commons.client.RetryRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by chris on 14/7/2017.
 */
@Configuration
public class AnonClientConfig {
    @Value("${anon.protocol:http}")
    private String protocol;

    @Value("${anon.host:localhost}")
    private String host;

    @Value("${anon.port:8085}")
    private String port;

    @Bean(name = "anonClient")
    public AnonClient anonClient(){
        return new AnonClientImpl();
    }

    @Autowired
    @Qualifier("CspRestTemplate")
    RetryRestTemplate retryRestTemplate;

}
