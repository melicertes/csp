package com.intrasoft.csp.ccs.client.config;

import com.intrasoft.csp.ccs.client.CcsClient;
import com.intrasoft.csp.ccs.client.impl.CcsClientImpl;
import com.intrasoft.csp.ccs.commons.client.ApiVersionClient;
import com.intrasoft.csp.ccs.commons.client.ApiVersionClientImpl;
import com.intrasoft.csp.ccs.commons.client.RetryRestTemplate;
import com.intrasoft.csp.ccs.commons.routes.ApiContextUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CcsClientConfig implements ApiContextUrl {

    @Value("${ccs.server.protocol:http}")
    private String protocol;

    @Value("${ccs.server.host:localhost}")
    private String host;

    @Value("${ccs.server.port:8090}")
    private String port;

    @Bean(name = "ccsClient")
    public CcsClient ccsClient(){
        return new CcsClientImpl();
    }

    @Autowired
    @Qualifier("CspRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Bean(name = "CcsApiVersionClient")
    public ApiVersionClient getApiVersionClient() throws Exception {
        return new ApiVersionClientImpl(protocol, host, port, API_V1, retryRestTemplate);
    }

}
