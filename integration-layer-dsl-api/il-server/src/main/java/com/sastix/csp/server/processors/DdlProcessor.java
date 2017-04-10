package com.sastix.csp.server.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.IntegrationDataType;
import com.sastix.csp.commons.routes.CamelRoutes;
import com.sastix.csp.commons.routes.HeaderName;
import org.apache.camel.*;

import org.apache.camel.impl.DefaultMessage;
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

    @Override
    public void process(Exchange exchange) throws Exception {
        String inData = exchange.getIn().getBody(String.class);
        IntegrationData integrationData = objectMapper.readValue(inData, IntegrationData.class);
        Boolean toShare = integrationData.getSharingParams().getToShare();

        List<String> recipients = new ArrayList<String>();

        if (toShare) {
            //recipients.add(CamelRoutes.DCL);
            producerTemplate.sendBody(CamelRoutes.DCL, ExchangePattern.InOut,integrationData);
        }

       //recipients.add(elasticURI + "/viper/" + indexType.toString().toLowerCase() + "?pretty");
        producerTemplate.sendBody(CamelRoutes.ELASTIC, ExchangePattern.InOut,integrationData);

        exchange.getIn().setHeader("recipients", recipients);
    }
}
