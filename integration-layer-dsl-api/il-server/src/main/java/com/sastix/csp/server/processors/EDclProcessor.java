package com.sastix.csp.server.processors;


import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.server.service.CspUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class EDclProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(EDclProcessor.class);

    private static HashMap<String, String> dataTypesAppMapping = new HashMap<>();
    private List<String> ecsps = new ArrayList<String>();

    @Autowired
    CspUtils cspUtils;

    @Override
    public void process(Exchange exchange) throws IOException {

        IntegrationData integrationData = cspUtils.getExchangeData(exchange, IntegrationData.class);

        LOG.info("Received integrationData from external CSP");
        LOG.info(exchange.getIn().getHeaders().toString());

        if (exchange.getIn().getHeader("method").equals("POST")){
            exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
        }
        else if (exchange.getIn().getHeader("method").equals("PUT")){
            exchange.getIn().setHeader(Exchange.HTTP_METHOD, "PUT");
        }

        List<String> ecsps = new ArrayList<String>();
        integrationData.getSharingParams().setIsExternal(true);
    }
}
