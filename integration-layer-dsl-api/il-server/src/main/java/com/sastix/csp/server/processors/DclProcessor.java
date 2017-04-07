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

import static com.sastix.csp.server.external.TrustCircles.getTrustCircle;

@Component
public class DclProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(DclProcessor.class);

    private List<String> ecsps = new ArrayList<String>();

    @Override
    public void process(Exchange exchange) throws IOException {

        IntegrationData integrationData = exchange.getIn().getBody(IntegrationData.class);

        logger.info("Received integrationData from DSL");
        /**
         * @TODO Anonymize data
         */


        /**
         * Get Recipients from Trust Circles
         */
        ecsps = getTrustCircle();

        exchange.getIn().setHeader("ecsps", ecsps);
        logger.info(exchange.getIn().getHeader("ecsps").toString());
    }
}
