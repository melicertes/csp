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
public class DclProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(DclProcessor.class);

    private static HashMap<String, String> dataTypesAppMapping = new HashMap<>();
    private static final String DELIMITER = ",";
    private static final String TC_ADAPRTER_URI = "http://localhost:8081/tc/";
    private static final String ECSP_ADAPRTER_URI = "http://localhost:{{server.port}}/ecsp/";

    @Override
    public void process(Exchange exchange) throws Exception {

        IntegrationData integrationData = exchange.getIn().getBody(IntegrationData.class);

        if (exchange.getFromEndpoint().getEndpointUri().toString().equals("direct://edcl")){

            logger.info("Comming from external CSP");
            List<String> ecsps = new ArrayList<String>();
            ecsps.add("http://localhost:8081/ecsp");
            integrationData.getSharingParams().setIsExternal(true);
            exchange.getIn().setHeader("ecsps", ecsps);
        }
        else {
            logger.info("Comming from DSL");
            /**
             * @TODO Anonymize data
             */

            /**
             * Get Recipients from Trust Circles
             */
            List<String> ecsps = new ArrayList<String>();
            ecsps = getTrustCircle();

            exchange.getIn().setHeader("ecsps", ecsps);
            logger.info("External csp list: " + ecsps.toString());
        }

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
