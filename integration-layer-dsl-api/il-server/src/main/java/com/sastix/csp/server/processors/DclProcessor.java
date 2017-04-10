package com.sastix.csp.server.processors;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sastix.csp.client.TrustCirclesClient;
import com.sastix.csp.commons.model.Csp;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.TrustCircle;
import com.sastix.csp.commons.model.TrustCircleEcspDTO;
import com.sastix.csp.commons.routes.CamelRoutes;
import com.sastix.csp.commons.routes.ContextUrl;
import com.sastix.csp.server.service.CamelRestService;
import org.apache.camel.*;
import org.apache.camel.impl.DefaultMessage;
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
    ObjectMapper objectMapper;

    @Autowired
    TrustCirclesClient tcClient;

    @Autowired
    CamelRestService camelRestService;

    @Produce
    private ProducerTemplate producerTemplate;

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
            //deprecated
            //ecsps = getTrustCircle();

            //using sastix client logic
            //ecsps = tcClient.getCsps("localhost");

            //direct http post through camel
            //TrustCircle tc = camelRestService.send(tcClient.getContext()+ ContextUrl.TRUST_CIRCLE,new Csp("localhost"), TrustCircle.class);

            // with camel response
            byte[] data = (byte[]) producerTemplate.sendBody(CamelRoutes.TC, ExchangePattern.InOut,new Csp("localhost"));
            TrustCircle tc = objectMapper.readValue(data, TrustCircle.class);

            TrustCircleEcspDTO trustCircleEcspDTO = new TrustCircleEcspDTO(tc,integrationData);

            producerTemplate.sendBody(CamelRoutes.ECSP, ExchangePattern.InOut,trustCircleEcspDTO);

//            ecsps = tc.getCsps();
//            exchange.getIn().setHeader("ecsps", ecsps);
//            logger.info(exchange.getIn().getHeader("ecsps").toString());
        }catch (Exception e){
            //TODO: handle this situation
            logger.error("TC api call failed.",e);
        }
    }
}
