package com.intrasoft.csp.server.routes;

import com.intrasoft.csp.commons.model.Team;
import com.intrasoft.csp.commons.routes.CamelRoutes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * Created by iskitsas on 4/27/17.
 */
@Service
public class RouteUtils implements CamelRoutes {
    @Value("${apache.camel.use.activemq}")
    Boolean useActiveMQ;

    @Value("${server.camel.default.endpoint:direct}")
    String defaultEndpoint;

    private String endpoint;

    @PostConstruct
    public void init(){
        endpoint = StringUtils.isEmpty(defaultEndpoint)?DIRECT:defaultEndpoint;
        if(useActiveMQ != null && useActiveMQ){
            endpoint = ACTIVEMQ;
        }
    }

    public String wrap(String context){
        String ret =  endpoint+":"+(useActiveMQ != null && useActiveMQ?context.toUpperCase()+".Q":context);
        return ret;
    }

    public String safeQueueName(Team team) {
        return Long.toHexString(String.format("%s:%s", team.getCspId(),team.getCountry()).hashCode());
    }
}
