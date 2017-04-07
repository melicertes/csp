package com.sastix.csp.server.processors;


import com.sastix.csp.commons.model.Csp;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.TrustCircle;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class EDclProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(EDclProcessor.class);

    private static HashMap<String, String> dataTypesAppMapping = new HashMap<>();
    private List<String> ecsps = new ArrayList<String>();

    @Override
    public void process(Exchange exchange) {

        IntegrationData integrationData = exchange.getIn().getBody(IntegrationData.class);

        logger.info("Received integrationData from external CSP");
        logger.info(exchange.getIn().getHeaders().toString());

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
