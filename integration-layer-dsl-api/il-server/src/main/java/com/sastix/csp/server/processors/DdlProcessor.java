package com.sastix.csp.server.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.routes.CamelRoutes;
import com.sastix.csp.server.service.CspUtils;
import org.apache.camel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class DdlProcessor implements Processor {
    @Autowired
    ObjectMapper objectMapper;

    @Value("${elastic.uri}")
    String elasticURI;

    @Produce
    ProducerTemplate producerTemplate;

    @Autowired
    CspUtils cspUtils;

    @Override
    public void process(Exchange exchange) throws Exception {
        IntegrationData integrationData = cspUtils.getExchangeData(exchange, IntegrationData.class);
        Boolean toShare = integrationData.getSharingParams().getToShare();

        List<String> recipients = new ArrayList<String>();

        if (toShare) {
            //recipients.add(CamelRoutes.DCL);
            producerTemplate.sendBody(CamelRoutes.DCL, ExchangePattern.InOut,integrationData);
        }

        producerTemplate.sendBody(CamelRoutes.ELASTIC, ExchangePattern.InOut,integrationData);
        //recipients.add(CamelRoutes.ELASTIC);
        exchange.getIn().setHeader("recipients", recipients);
//        exchange.getIn().setHeader(Exchange.HTTP_METHOD,"POST");
        exchange.getIn().setBody(integrationData);
    }
}
