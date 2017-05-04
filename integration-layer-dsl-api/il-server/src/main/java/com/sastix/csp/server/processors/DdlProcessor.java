package com.sastix.csp.server.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.routes.CamelRoutes;
import com.sastix.csp.server.routes.RouteUtils;
import com.sastix.csp.server.service.CspUtils;
import org.apache.camel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class DdlProcessor implements Processor,CamelRoutes {
    @Autowired
    ObjectMapper objectMapper;

    @Produce
    ProducerTemplate producerTemplate;

    @Autowired
    RouteUtils routes;

    @Autowired
    CspUtils cspUtils;

    @Override
    public void process(Exchange exchange) throws Exception {
        IntegrationData integrationData = cspUtils.getExchangeData(exchange, IntegrationData.class);
        Boolean toShare = integrationData.getSharingParams().getToShare();

        List<String> recipients = new ArrayList<>();

        if (toShare) {
            recipients.add(routes.apply(DCL));
            //producerTemplate.sendBodyAndHeader(routes.apply(DCL), ExchangePattern.InOut,integrationData, Exchange.HTTP_METHOD, httpMethod);
        }

        //producerTemplate.sendBodyAndHeader(routes.apply(ELASTIC), ExchangePattern.InOut,integrationData, Exchange.HTTP_METHOD, httpMethod);
        recipients.add(routes.apply(ELASTIC));
        exchange.getIn().setHeader("recipients", recipients);
//        exchange.getIn().setHeader(Exchange.HTTP_METHOD,httpMethod);
        exchange.getIn().setBody(integrationData);
    }
}
