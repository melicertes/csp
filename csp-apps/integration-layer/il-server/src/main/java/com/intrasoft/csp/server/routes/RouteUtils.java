package com.intrasoft.csp.server.routes;

import com.intrasoft.csp.commons.model.Team;
import com.intrasoft.csp.commons.routes.CamelRoutes;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * Created by iskitsas on 4/27/17.
 */
@Service
public class RouteUtils implements CamelRoutes {
    private static final Logger LOG = LoggerFactory.getLogger(RouteUtils.class);

    private static final Base64 B64 = new Base64(true);
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

    /**
     * create a safe queue name after
     * @param team
     * @return
     */
    public String safeQueueName(Team team) {
        final String queuePart = String.format("%s_%s", team.getCspId(), team.getCountry());
        LOG.debug("QPart {} for {}:{}", queuePart, team.getId(), team.getCountry());
        return queuePart;
    }
}
