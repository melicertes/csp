package com.sastix.csp.server.processors;

import com.sastix.csp.commons.model.*;
import com.sastix.csp.commons.routes.ContextUrl;
import com.sastix.csp.server.config.CspSslConfiguration;
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

    @Autowired
    CspSslConfiguration cspSslConfiguration;

    @Override
    public void process(Exchange exchange) throws Exception {

        String httpMethod = (String) exchange.getIn().getHeader(Exchange.HTTP_METHOD);
        EnhancedTeamDTO enhancedTeamDTO = exchange.getIn().getBody(EnhancedTeamDTO.class);
        LOG.info("DCL - Sending to external CSP: " + enhancedTeamDTO.getTeam().getName() + " -- " + enhancedTeamDTO.getTeam().getUrl());
        String uri = enhancedTeamDTO.getTeam().getUrl() + ContextUrl.DCL_INTEGRATION_DATA;
        //http4-ecsp //external certificate
        if(cspSslConfiguration.getExternalUseSSL()){
            uri = uri.replaceAll("http",cspSslConfiguration.getExternalSslEndpointProtocol());
        }
        String response = camelRestService.send(uri, enhancedTeamDTO.getIntegrationData(), httpMethod);

    }
}
