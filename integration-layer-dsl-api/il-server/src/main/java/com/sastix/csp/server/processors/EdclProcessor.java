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
public class EdclProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(EdclProcessor.class);

    private static HashMap<String, String> dataTypesAppMapping = new HashMap<>();
    private List<String> ecsps = new ArrayList<String>();

    @Autowired
    CspUtils cspUtils;

    @Override
    public void process(Exchange exchange) throws IOException {
        IntegrationData integrationData = cspUtils.getExchangeData(exchange, IntegrationData.class);
        String httpMethod = (String) exchange.getIn().getHeader(Exchange.HTTP_METHOD);

        exchange.getIn().setHeader(Exchange.HTTP_METHOD, httpMethod);

        List<String> ecsps = new ArrayList<String>();
        integrationData.getSharingParams().setIsExternal(true);
    }
}
