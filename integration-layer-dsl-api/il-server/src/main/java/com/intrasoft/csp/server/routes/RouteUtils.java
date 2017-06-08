package com.intrasoft.csp.server.routes;

import com.intrasoft.csp.commons.routes.CamelRoutes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by iskitsas on 4/27/17.
 */
@Service
public class RouteUtils implements CamelRoutes {
    @Value("${apache.camel.use.activemq}")
    Boolean useActiveMQ;

    private String endpoint;

    @PostConstruct
    public void init(){
        endpoint = DIRECT;
        if(useActiveMQ != null && useActiveMQ){
            endpoint = ACTIVEMQ;
        }
    }

    public String apply(String context){
        String ret =  endpoint+":"+(useActiveMQ != null && useActiveMQ?context.toUpperCase()+".Q":context);
        return ret;
    }
}
