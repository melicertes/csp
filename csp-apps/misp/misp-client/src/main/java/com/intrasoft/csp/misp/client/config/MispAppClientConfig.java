package com.intrasoft.csp.misp.client.config;

import com.intrasoft.csp.misp.client.MispAppClient;
import com.intrasoft.csp.misp.client.impl.MispAppClientImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MispAppClientConfig {

    @Value("${misp.app.protocol}")
    private String protocol;

    @Value("${misp.app.host}")
    private String host;

    @Value("${misp.app.port}")
    private String port;

    @Value("${misp.app.authorization.key}")
    private String authorizationKey;

    @Bean(name = "mispAppClient")
    public MispAppClient addMispEvent(){
        MispAppClient client = new MispAppClientImpl();
        client.setProtocolHostPortHeaders(protocol,host,port, authorizationKey);
        return client;
    }

}
