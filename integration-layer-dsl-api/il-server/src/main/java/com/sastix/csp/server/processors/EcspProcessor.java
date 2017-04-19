package com.sastix.csp.server.processors;

import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.Team;
import com.sastix.csp.commons.model.TrustCircle;
import com.sastix.csp.commons.model.TrustCircleEcspDTO;
import com.sastix.csp.server.service.CamelRestService;
import org.apache.camel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by iskitsas on 4/10/17.
 */
@Component
public class EcspProcessor implements Processor{
    private static final Logger LOG = LoggerFactory.getLogger(EcspProcessor.class);
    @Produce
    ProducerTemplate producerTemplate;

    @Autowired
    CamelRestService camelRestService;

    @Override
    public void process(Exchange exchange) throws Exception {
        TrustCircleEcspDTO trustCircleEcspDTO = exchange.getIn().getBody(TrustCircleEcspDTO.class);
        TrustCircle tc = trustCircleEcspDTO.getTrustCircle();
        IntegrationData integrationData = trustCircleEcspDTO.getIntegrationData();
        String httpMethod = (String) exchange.getIn().getHeader(Exchange.HTTP_METHOD);

        List<Team> ecsps = trustCircleEcspDTO.getTeams();
        for (Team ecsp : ecsps) {
            camelRestService.send(ecsp.getUrl(),integrationData,httpMethod);
        }
        //exchange.getIn().setHeader(Exchange.HTTP_METHOD, httpMethod);
        //exchange.getIn().setHeader("ecsps", ecsps);
        //exchange.getIn().setBody(integrationData);
    }
}
