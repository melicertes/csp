package com.intrasoft.csp.server.processors;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.routes.CamelRoutes;
import com.intrasoft.csp.server.routes.RouteUtils;
import com.intrasoft.csp.server.service.CspUtils;
import org.apache.camel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class EdclProcessor implements Processor,CamelRoutes {

    private static final Logger LOG = LoggerFactory.getLogger(EdclProcessor.class);

    @Autowired
    RouteUtils routes;

    @Autowired
    CspUtils cspUtils;

    @Produce
    private ProducerTemplate producerTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void process(Exchange exchange) throws IOException {
        LOG.info("DCL - received integrationData from external CSP");

        IntegrationData integrationData = cspUtils.getExchangeData(exchange, IntegrationData.class);
        String httpMethod = (String) exchange.getIn().getHeader(Exchange.HTTP_METHOD);

        exchange.getIn().setHeader(Exchange.HTTP_METHOD, httpMethod);
        //pass message for TC processing
        exchange.getIn().setBody(integrationData);
        exchange.getIn().setHeader(CamelRoutes.ORIGIN_ENDPOINT, routes.apply(EDCL));
        exchange.getIn().setHeader("recipients", routes.apply(TC));

    }
}
