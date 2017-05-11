package com.sastix.csp.server.processors;

import com.sastix.csp.commons.model.*;
import com.sastix.csp.commons.routes.ContextUrl;
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
        /*TrustCircleEcspDTO trustCircleEcspDTO = exchange.getIn().getBody(TrustCircleEcspDTO.class);
        TrustCircle tc = trustCircleEcspDTO.getTrustCircle();
        IntegrationData integrationData = trustCircleEcspDTO.getIntegrationData();
        integrationData.getSharingParams().setIsExternal(true);
        integrationData.getSharingParams().setToShare(false);
        String httpMethod = (String) exchange.getIn().getHeader(Exchange.HTTP_METHOD);

        List<Team> teams = trustCircleEcspDTO.getTeams();
        for (Team team : teams) {//TODO: this is wrong.
            // One request should be executed in this processor.
            // In case of failure you should try to redeliver one request and not a batch of requests.
            // Move the for loop to previous processor or create a new one
            LOG.info("DCL - Sending to external CSP: " + team.getName() + " -- " + team.getUrl());
            String uri = team.getUrl() + ContextUrl.DCL_INTEGRATION_DATA;
            String response = camelRestService.send(uri, integrationData, httpMethod);
        }*/

        String httpMethod = (String) exchange.getIn().getHeader(Exchange.HTTP_METHOD);
        EnhancedTeamDTO enhancedTeamDTO = exchange.getIn().getBody(EnhancedTeamDTO.class);
        LOG.info("DCL - Sending to external CSP: " + enhancedTeamDTO.getTeam().getName() + " -- " + enhancedTeamDTO.getTeam().getUrl());
        String uri = enhancedTeamDTO.getTeam().getUrl() + ContextUrl.DCL_INTEGRATION_DATA;
        String response = camelRestService.send(uri, enhancedTeamDTO.getIntegrationData(), httpMethod);



    }
}
