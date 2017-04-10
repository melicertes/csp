package com.sastix.csp.server.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sastix.csp.client.TrustCirclesClient;
import com.sastix.csp.commons.model.Csp;
import com.sastix.csp.commons.model.TrustCircle;
import com.sastix.csp.commons.routes.ContextUrl;
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
 * Created by iskitsas on 4/9/17.
 */
@Component
public class TcProcessor implements Processor{
    private static final Logger LOG = LoggerFactory.getLogger(TcProcessor.class);
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CamelRestService camelRestService;

    @Autowired
    TrustCirclesClient tcClient;

    @Override
    public void process(Exchange exchange) throws Exception {
        Csp csp = exchange.getIn().getBody(Csp.class);
        TrustCircle tc = camelRestService.send(tcClient.getContext()+ ContextUrl.TRUST_CIRCLE,csp, TrustCircle.class);
        Message m = new DefaultMessage();
        m.setBody(tc);
        exchange.setOut(m);
    }
}
