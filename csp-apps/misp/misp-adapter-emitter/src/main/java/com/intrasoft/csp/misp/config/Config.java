package com.intrasoft.csp.misp.config;

import com.intrasoft.csp.client.CspClient;
import com.intrasoft.csp.client.impl.CspClientImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({
        "com.intrasoft.csp.client.config",
        "com.intrasoft.csp.libraries.versioning",
        "com.intrasoft.csp.commons.routes"})
public class Config {

/*    @Autowired
    @Qualifier("cspClient")
    CspClient cspClient;

    @Bean(name = "cspClient")
    public CspClient cspClient(){
        return new CspClientImpl();
    }*/


}
