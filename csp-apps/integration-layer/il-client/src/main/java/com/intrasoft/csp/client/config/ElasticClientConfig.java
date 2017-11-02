package com.intrasoft.csp.client.config;

import com.intrasoft.csp.client.ElasticClient;
import com.intrasoft.csp.client.impl.ElasticClientImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticClientConfig {

    @Value("${elastic.protocol}")
    String elasticProtocol;
    @Value("${elastic.host}")
    String elasticHost;
    @Value("${elastic.port}")
    String elasticPort;
    @Value("${elastic.path}")
    String elasticPath;

    @Bean(name = "elasticClient")
    public ElasticClient getElasticClient(){
        ElasticClient elasticClient = new ElasticClientImpl();
        elasticClient.setProtocolHostPort(elasticProtocol,elasticHost,elasticPort);
        return elasticClient;
    }
}
