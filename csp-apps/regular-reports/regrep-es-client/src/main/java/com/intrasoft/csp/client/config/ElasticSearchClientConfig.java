package com.intrasoft.csp.client.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.client.ElasticSearchClient;
import com.intrasoft.csp.client.impl.ElasticSearchClientImpl;
import com.intrasoft.csp.client.routes.ContextUrl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ElasticSearchClientConfig implements ContextUrl {

    @Value("${es.protocol}")
    private String protocol;
    @Value("${es.host}")
    private String host;
    @Value("${es.port}")
    private String port;

    @Bean(name = "elasticSearchClient")
    public ElasticSearchClient getElasticSearchClient(){
        ElasticSearchClient elasticSearchClient = new ElasticSearchClientImpl(getElasticSearchBaseContext());
        elasticSearchClient.setProtocolHostPort(protocol,host,port);
        return elasticSearchClient;
    }

    @Bean(name = "elasticSearchClientRestTemplate")
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean(name = "elasticSearchClientObjectMapper")
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }


    public String getElasticSearchBaseContext(){
        return protocol + "://" + host + ":" + port;
    }

}
