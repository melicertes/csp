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
    private static final String TC_ADAPRTER_URI = "http://localhost:8081/tc";
    private List<String> ecsps = new ArrayList<String>();

    @Override
    public void process(Exchange exchange) {

        IntegrationData integrationData = exchange.getIn().getBody(IntegrationData.class);

            logger.info("Received integrationData from external CSP");
            List<String> ecsps = new ArrayList<String>();
            integrationData.getSharingParams().setIsExternal(true);
    }

    private List<String> getTrustCircle() throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        logger.info("Get from: " + TC_ADAPRTER_URI);
        HttpEntity<Csp> request = new HttpEntity<Csp>(new Csp("localhost"));
        TrustCircle tc = restTemplate.postForObject(TC_ADAPRTER_URI, request, TrustCircle.class);
        logger.info(tc.toString());
        return tc.getCsps();
    }
}
