package com.sastix.csp.server.processors;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sastix.csp.client.TrustCirclesClient;
import com.sastix.csp.commons.model.*;
import com.sastix.csp.commons.routes.CamelRoutes;
import com.sastix.csp.commons.routes.ContextUrl;
import com.sastix.csp.server.routes.RouteUtils;
import com.sastix.csp.server.service.CamelRestService;
import com.sastix.csp.server.service.CspUtils;
import org.apache.camel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DclProcessor implements Processor,CamelRoutes {

    private static final Logger LOG = LoggerFactory.getLogger(DclProcessor.class);

    private List<String> ecsps = new ArrayList<String>();
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TrustCirclesClient tcClient;

    @Autowired
    CamelRestService camelRestService;

    @Autowired
    CspUtils cspUtils;

    @Autowired
    RouteUtils routes;

    @Produce
    private ProducerTemplate producerTemplate;

    @Override
    public void process(Exchange exchange) throws IOException {
        IntegrationData integrationData = cspUtils.getExchangeData(exchange,IntegrationData.class);
        LOG.info("DCL - received integrationData with datatype: " + integrationData.getDataType());
        String httpMethod = (String) exchange.getIn().getHeader(Exchange.HTTP_METHOD);

        /**
         * @TODO Anonymize data
         */

        //pass message for TC processing
        exchange.getIn().setBody(integrationData);
        exchange.getIn().setHeader(CamelRoutes.ORIGIN_ENDPOINT, routes.apply(DCL));
        exchange.getIn().setHeader("recipients", routes.apply(TC));
    }
}
