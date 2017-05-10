package com.sastix.csp.server.processors;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sastix.csp.commons.model.*;
import com.sastix.csp.commons.routes.CamelRoutes;
import com.sastix.csp.server.routes.RouteUtils;
import com.sastix.csp.server.service.CspUtils;
import org.apache.camel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class EdclProcessor implements Processor,CamelRoutes {

    private static final Logger LOG = LoggerFactory.getLogger(EdclProcessor.class);

    @Autowired
    RouteUtils routes;

    private static HashMap<String, String> dataTypesAppMapping = new HashMap<>();
    private List<String> ecsps = new ArrayList<String>();

    @Autowired
    CspUtils cspUtils;

    @Produce
    private ProducerTemplate producerTemplate;

    @Autowired
    ObjectMapper objectMapper;

    private boolean authorized = false;

    @Override
    public void process(Exchange exchange) throws IOException {
        LOG.info("DCL - received integrationData from external CSP");
        List<String> recipients = new ArrayList<>();
        IntegrationData integrationData = cspUtils.getExchangeData(exchange, IntegrationData.class);
        String httpMethod = (String) exchange.getIn().getHeader(Exchange.HTTP_METHOD);

        exchange.getIn().setHeader(Exchange.HTTP_METHOD, httpMethod);

        Integer datatypeId = integrationData.getDataType().ordinal();
        byte[] data = (byte[]) producerTemplate.sendBodyAndHeader(routes.apply(TC), ExchangePattern.InOut,new Csp(datatypeId), Exchange.HTTP_METHOD, "GET");
        TrustCircle tc = objectMapper.readValue(data, TrustCircle.class);

        TrustCircleEcspDTO trustCircleEcspDTO = new TrustCircleEcspDTO(tc, integrationData);
        List<Team> teams = new ArrayList<>();
        for (Integer id : tc.getTeams()){
            byte[] dataTeam = (byte[]) producerTemplate.sendBodyAndHeader(routes.apply(TCT), ExchangePattern.InOut, id, Exchange.HTTP_METHOD, "GET");
            Team team = objectMapper.readValue(dataTeam, Team.class);
            if (team.getCspId().equals(integrationData.getDataParams().getCspId())){
                LOG.info("DCL - " + team.getCspId() + " is Authorized");
                authorized = true;
            }
            else {
                LOG.info("DCL - " + team.getCspId() + " not Authorized");
            }
        }

        if (authorized){
            integrationData.getSharingParams().setIsExternal(true);
//            integrationData.getSharingParams().setToShare(false);
            recipients.add(routes.apply(DSL));
            exchange.getIn().setHeader("recipients", recipients);
        }

    }
}
