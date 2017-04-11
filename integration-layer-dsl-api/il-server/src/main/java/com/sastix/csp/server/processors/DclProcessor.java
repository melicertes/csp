package com.sastix.csp.server.processors;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sastix.csp.client.TrustCirclesClient;
import com.sastix.csp.commons.model.Csp;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.TrustCircle;
import com.sastix.csp.commons.model.TrustCircleEcspDTO;
import com.sastix.csp.commons.routes.CamelRoutes;
import com.sastix.csp.server.service.CamelRestService;
import com.sastix.csp.server.service.CspUtils;
import org.apache.camel.*;
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

    @Autowired
    CspUtils cspUtils;

    @Produce
    private ProducerTemplate producerTemplate;

    @Override
    public void process(Exchange exchange) throws IOException {
        logger.info("CSL Processing integrationData (from DSL)");
        IntegrationData integrationData = cspUtils.getExchangeData(exchange,IntegrationData.class);
        String httpMethod = (String) exchange.getIn().getHeader(Exchange.HTTP_METHOD);

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
            byte[] data = (byte[]) producerTemplate.sendBodyAndHeader(CamelRoutes.TC, ExchangePattern.InOut,new Csp("localhost"), Exchange.HTTP_METHOD, httpMethod);
            TrustCircle tc = objectMapper.readValue(data, TrustCircle.class);

            TrustCircleEcspDTO trustCircleEcspDTO = new TrustCircleEcspDTO(tc,integrationData);

            //producerTemplate.sendBodyAndHeader(CamelRoutes.ECSP, ExchangePattern.InOut,trustCircleEcspDTO, Exchange.HTTP_METHOD, httpMethod);
            exchange.getIn().setHeader("recipients", CamelRoutes.ECSP);
            exchange.getIn().setBody(trustCircleEcspDTO);
        }catch (Exception e){
            //TODO: handle this situation
            logger.error("TC api call failed.",e);
        }
    }
}
