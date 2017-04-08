package com.sastix.csp.server.processors;


import com.sastix.csp.client.TrustCirclesClient;
import com.sastix.csp.commons.model.IntegrationData;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DclProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(DclProcessor.class);

    private List<String> ecsps = new ArrayList<String>();

    @Autowired
    TrustCirclesClient tcClient;

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
        try {
            //TODO: can it be done through camel?
            //ecsps = getTrustCircle();
            ecsps = tcClient.getCsps("localhost");
            exchange.getIn().setHeader("ecsps", ecsps);
            logger.info(exchange.getIn().getHeader("ecsps").toString());
        }catch (Exception e){
            //TODO: handle this situation
            logger.error("TC api call failed.",e);
        }
    }
}
