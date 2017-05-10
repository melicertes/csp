package com.sastix.csp.server.processors;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sastix.csp.client.TrustCirclesClient;
import com.sastix.csp.commons.model.*;
import com.sastix.csp.commons.routes.CamelRoutes;
import com.sastix.csp.server.routes.RouteUtils;
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
public class DclProcessor implements Processor,CamelRoutes {

    private static final Logger LOG = LoggerFactory.getLogger(DclProcessor.class);

    private List<String> ecsps = new ArrayList<String>();
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TrustCirclesClient tcClient;

    @Autowired
    CamelRestService camelRestService;

    @Autowired
    CspUtils cspUtils;

    @Autowired
    RouteUtils routes;

    @Produce
    private ProducerTemplate producerTemplate;

    @Override
    public void process(Exchange exchange) throws IOException {
        LOG.info("CSL Processing integrationData (from DSL)");
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
            // -----> TODO: move to TC processor
            Integer datatypeId = integrationData.getDataType().ordinal();
            byte[] data = (byte[]) producerTemplate.sendBodyAndHeader(routes.apply(TC), ExchangePattern.InOut,new Csp(datatypeId), Exchange.HTTP_METHOD, "GET");
            TrustCircle tc = objectMapper.readValue(data, TrustCircle.class);

            TrustCircleEcspDTO trustCircleEcspDTO = new TrustCircleEcspDTO(tc, integrationData);
            List<Team> teams = new ArrayList<>();
            for (Integer id : tc.getTeams()){
                byte[] dataTeam = (byte[]) producerTemplate.sendBodyAndHeader(routes.apply(TCT), ExchangePattern.InOut, id, Exchange.HTTP_METHOD, "GET");
                Team team = objectMapper.readValue(dataTeam, Team.class);
                teams.add(team);
            }
            // -----<

            trustCircleEcspDTO.setTeams(teams);
            //AVOID this: producerTemplate.sendBodyAndHeader(routes.apply(ECSP), ExchangePattern.InOut, trustCircleEcspDTO, Exchange.HTTP_METHOD, httpMethod); // if used, ends in multicast ...
            exchange.getIn().setHeader("recipients", routes.apply(ECSP)); //TODO: this should be moved to TC processor
            exchange.getIn().setBody(trustCircleEcspDTO);


        }catch (Exception e){
            //TODO: handle this situation
            LOG.error("TC api call failed.",e);
        }
    }
}
