package com.sastix.csp.server.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sastix.csp.client.TrustCirclesClient;
import com.sastix.csp.commons.model.TrustCircle;
import com.sastix.csp.commons.model.TrustCircleEcspDTO;
import com.sastix.csp.server.service.CamelRestService;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by chris on 19/4/2017.
 */
@Component
public class TeamProcessor implements Processor {
    private static final Logger LOG = LoggerFactory.getLogger(TcProcessor.class);
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
        TrustCircle tc = camelRestService.send("http://csp.dangerduck.gr:8000/api/v1/teams/" + teamId,teamId, httpMethod, TrustCircle.class);
        Message m = new DefaultMessage();
        m.setBody(tc);
        exchange.setOut(m);
    }
}