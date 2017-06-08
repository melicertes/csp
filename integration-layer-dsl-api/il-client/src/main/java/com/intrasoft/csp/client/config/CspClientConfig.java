package com.intrasoft.csp.client.config;

import com.intrasoft.csp.client.CspClient;
import com.intrasoft.csp.client.impl.CspClientImpl;
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
 * Created by iskitsas on 5/3/17.
 */
@Configuration
public class CspClientConfig implements ContextUrl {
    @Value("${csp.server.protocol:http}")
    private String protocol;

    @Value("${csp.server.host:localhost}")
    private String host;

    @Value("${csp.server.port:8081}")
    private String port;

    @Bean
    public CspClient cspClient(){
        return new CspClientImpl();
    }

    @Autowired
    @Qualifier("CspRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Bean(name = "CspApiVersionClient")
    public ApiVersionClient getApiVersionClient() throws Exception {
        return new ApiVersionClientImpl(protocol, host, port, REST_API_V1, retryRestTemplate);
    }
}
