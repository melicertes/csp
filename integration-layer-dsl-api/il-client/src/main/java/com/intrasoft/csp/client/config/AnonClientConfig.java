package com.intrasoft.csp.client.config;

import com.intrasoft.csp.client.AnonClient;
import com.intrasoft.csp.client.impl.AnonClientImpl;
import com.intrasoft.csp.commons.client.ApiVersionClient;
import com.intrasoft.csp.commons.client.ApiVersionClientImpl;
import com.intrasoft.csp.commons.client.RetryRestTemplate;
import com.intrasoft.csp.commons.routes.ContextUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by chris on 14/7/2017.
 */
@Configuration
public class AnonClientConfig implements ContextUrl {
    @Value("${anon.server.protocol:http}")
    private String protocol;

    @Value("${anon.server.host:localhost}")
    private String host;

    @Value("${anon.server.port:8085}")
    private String port;

    @Bean(name = "anonClient")
    public AnonClient anonClient(){
        return new AnonClientImpl();
    }

    @Autowired
    @Qualifier("CspRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Bean(name = "AnonApiVersionClient")
    public ApiVersionClient getApiVersionClient() throws Exception {
        return new ApiVersionClientImpl(protocol, host, port, REST_API_V1, retryRestTemplate);
    }

}
