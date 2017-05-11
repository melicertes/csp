package com.sastix.csp.server.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sastix.csp.client.TrustCirclesClient;
import com.sastix.csp.commons.model.*;
import com.sastix.csp.commons.routes.CamelRoutes;
import com.sastix.csp.commons.routes.ContextUrl;
import com.sastix.csp.commons.routes.HeaderName;
import com.sastix.csp.server.routes.RouteUtils;
import com.sastix.csp.server.service.CamelRestService;
import org.apache.camel.*;
import org.apache.camel.impl.DefaultMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by iskitsas on 4/9/17.
 */
@Component
public class TcProcessor implements Processor,CamelRoutes{
    private static final Logger LOG = LoggerFactory.getLogger(TcProcessor.class);

    @Value("${tc.protocol}")
    String tcProtocol;
    @Value("${tc.host}")
    String tcHost;
    @Value("${tc.port}")
    String tcPort;
    @Value("${tc.path.circles}")
    String tcPathCircles;
    @Value("${tc.path.teams}")
    String tcPathTeams;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CamelRestService camelRestService;

    @Autowired
    ProducerTemplate producer;


    @Autowired
    RouteUtils routes;

    @Override
    public void process(Exchange exchange) throws Exception {
        String originEndpoint = (String) exchange.getIn().getHeader(CamelRoutes.ORIGIN_ENDPOINT);
        String msg = originEndpoint.equals(routes.apply(CamelRoutes.DCL))? "send to external CSP":
                originEndpoint.equals(routes.apply(CamelRoutes.EDCL))? " handle from external CSP":"";
        LOG.info("DCL - Get Trust Circles from TC API and "+msg+" [ORIGIN_ENDPOINT:"+originEndpoint+"]");


//        TrustCircleEcspDTO trustCircleEcspDTO = exchange.getIn().getBody(TrustCircleEcspDTO.class);
        //Csp csp = exchange.getIn().getBody(Csp.class);



        IntegrationData integrationData = exchange.getIn().getBody(IntegrationData.class);
        String httpMethod = (String) exchange.getIn().getHeader(Exchange.HTTP_METHOD);

        Integer datatypeId = integrationData.getDataType().ordinal();
        Csp csp = new Csp(datatypeId);

        //make all TC calls
        String uri = this.getTcCirclesURI() + "/" + csp.getCspId();
        TrustCircle tc = camelRestService.send(uri, csp,  HttpMethod.GET.name(), TrustCircle.class);

        List<Team> teams = new ArrayList<>();
        //first make all calls to get the teams
        for (Integer teamId : tc.getTeams()){
            //make call to TC-team
            Team team = camelRestService.send(this.getTcTeamsURI() + "/" + teamId, teamId, HttpMethod.GET.name(), Team.class);
            teams.add(team);
        }
        //all TC calls have been made up to this point, TEAMS list has been populated

        // Decide the flow
        if(originEndpoint.equals(routes.apply(CamelRoutes.DCL))) {
            for(Team team:teams) {
                //send to ECSP
                handleDclFlowAndSendToECSP(httpMethod, team, integrationData);
            }
        }else if(originEndpoint.equals(routes.apply(CamelRoutes.EDCL))){
            handleExternalDclFlowAndSendToDSL(exchange, teams, integrationData);
        }




//        Message m = new DefaultMessage();
//        m.setBody(tc);
//        exchange.setOut(m);
    }

/*    private String getThreatVal(Csp csp) {
        String shortNameVal = "CTC::";
        if (csp.getCspId().equals(IntegrationDataType.ARTEFACT.name())) {
            shortNameVal += "SHARING_DATA_ARTEFACT";
        } else if (csp.getCspId().equals(IntegrationDataType.CHAT.name())) {
            shortNameVal += "SHARING_DATA_CHAT";
        } else if (csp.getCspId().equals(IntegrationDataType.VULNERABILITY.name())) {
            shortNameVal += "SHARING_DATA_VULNERABILITY";
        } else if (csp.getCspId().equals(IntegrationDataType.CONTACT.name())) {
            shortNameVal += "SHARING_DATA_CONTACT";
        } else if (csp.getCspId().equals(IntegrationDataType.EVENT.name())) {
            shortNameVal += "SHARING_DATA_EVENT";
        } else if (csp.getCspId().equals(IntegrationDataType.FILE.name())) {
            shortNameVal += "SHARING_DATA_FILE";
        } else if (csp.getCspId().equals(IntegrationDataType.INCIDENT.name())) {
            shortNameVal += "SHARING_DATA_INCIDENT";
        } else if (csp.getCspId().trim().equals(IntegrationDataType.THREAT.name().trim())) {
            shortNameVal += "SHARING_DATA_THREAT";
        } else if (csp.getCspId().equals(IntegrationDataType.TRUSTCIRCLE.name())) {
            shortNameVal += "";
        } else {
            shortNameVal += "UNKNOWN";
        }
        LOG.info(shortNameVal);
        return shortNameVal;
    }*/

    private void handleDclFlowAndSendToECSP(String httpMethod, Team team, IntegrationData integrationData){
        EnhancedTeamDTO enhancedTeamDTO = new EnhancedTeamDTO(team, integrationData);
        Map<String, Object> headers = new HashMap<>();

        headers.put(Exchange.HTTP_METHOD, httpMethod);
        producer.sendBodyAndHeaders(routes.apply(ECSP), ExchangePattern.InOut, enhancedTeamDTO, headers);
    }

    private void handleExternalDclFlowAndSendToDSL(Exchange exchange,List<Team> teams, IntegrationData integrationData){
        boolean authorized = teams.stream().anyMatch(t->t.getCspId().equals(integrationData.getDataParams().getCspId()));
        if (authorized){
            integrationData.getSharingParams().setIsExternal(true);
            //integrationData.getSharingParams().setToShare(false);
            exchange.getIn().setBody(integrationData);
            exchange.getIn().setHeader("recipients", routes.apply(DSL));
        }
    }

    private String getTcCirclesURI() {
        return tcProtocol + "://" + tcHost + ":" + tcPort + tcPathCircles;
    }
    private String getTcTeamsURI() {
        return tcProtocol + "://" + tcHost + ":" + tcPort + tcPathTeams;
    }
}
