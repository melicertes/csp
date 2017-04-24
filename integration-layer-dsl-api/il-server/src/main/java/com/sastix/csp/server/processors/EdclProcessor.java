package com.sastix.csp.server.processors;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sastix.csp.commons.model.*;
import com.sastix.csp.commons.routes.CamelRoutes;
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
public class EdclProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(EdclProcessor.class);

    private static HashMap<String, String> dataTypesAppMapping = new HashMap<>();
    private List<String> ecsps = new ArrayList<String>();

    @Autowired
    CspUtils cspUtils;

    @Produce
    private ProducerTemplate producerTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void process(Exchange exchange) throws IOException {
        IntegrationData integrationData = cspUtils.getExchangeData(exchange, IntegrationData.class);
        String httpMethod = (String) exchange.getIn().getHeader(Exchange.HTTP_METHOD);

        exchange.getIn().setHeader(Exchange.HTTP_METHOD, httpMethod);

        Integer datatypeId = integrationData.getDataType().ordinal();
        byte[] data = (byte[]) producerTemplate.sendBodyAndHeader(CamelRoutes.TC, ExchangePattern.InOut,new Csp(datatypeId), Exchange.HTTP_METHOD, "GET");
        TrustCircle tc = objectMapper.readValue(data, TrustCircle.class);

        TrustCircleEcspDTO trustCircleEcspDTO = new TrustCircleEcspDTO(tc, integrationData);
        List<Team> teams = new ArrayList<>();
        for (Integer id : tc.getTeams()){
            byte[] dataTeam = (byte[]) producerTemplate.sendBodyAndHeader(CamelRoutes.TCT, ExchangePattern.InOut, id, Exchange.HTTP_METHOD, "GET");
            Team team = objectMapper.readValue(dataTeam, Team.class);
            teams.add(team);
        }

        integrationData.getSharingParams().setIsExternal(true);
    }
}
