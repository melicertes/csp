package com.sastix.csp.server.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sastix.csp.client.TrustCirclesClient;
import com.sastix.csp.commons.constants.AppProperties;
import com.sastix.csp.commons.model.*;
import com.sastix.csp.commons.routes.CamelRoutes;
import com.sastix.csp.commons.routes.HeaderName;
import com.sastix.csp.server.service.CamelRestService;
import org.apache.camel.*;
import org.apache.camel.impl.DefaultMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by chris on 19/4/2017.
 */
@Component
public class TeamProcessor implements Processor {
    private static final Logger LOG = LoggerFactory.getLogger(TcProcessor.class);

    @Value("${tc.protocol}")
    String tcProtocol;
    @Value("${tc.host}")
    String tcHost;
    @Value("${tc.port}")
    String tcPort;
    @Value("${tc.path.teams}")
    String tcPath;

    @Produce
    ProducerTemplate producerTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CamelRestService camelRestService;

    @Autowired
    TrustCirclesClient tcClient;

    @Override
    public void process(Exchange exchange) throws Exception {
        Integer teamId = exchange.getIn().getBody(Integer.class);
        String httpMethod = (String) exchange.getIn().getHeader(Exchange.HTTP_METHOD);
        Team team = camelRestService.send(this.getTcURI() + "/" + teamId, teamId, httpMethod, Team.class);

        Message m = new DefaultMessage();
        m.setBody(team);
        exchange.setOut(m);
    }


    private String getTcURI() {
        return tcProtocol + "://" + tcHost + ":" + tcPort + tcPath;
    }
}