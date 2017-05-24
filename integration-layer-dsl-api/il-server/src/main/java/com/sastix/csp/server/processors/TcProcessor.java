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

    @Value("${threat.id}")
    String threatId;
    @Value("${event.id}")
    String eventId;
    @Value("${artefact.id}")
    String artefactId;
    @Value("${incident.id}")
    String incidentId;
    @Value("${contact.id}")
    String contactId;
    @Value("${file.id}")
    String fileId;
    @Value("${chat.id}")
    String chatId;
    @Value("${vulnerability.id}")
    String vulnerabilityId;
    @Value("${trustCircle.id}")
    String trustCircleId;

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

        IntegrationData integrationData = exchange.getIn().getBody(IntegrationData.class);
        String httpMethod = (String) exchange.getIn().getHeader(Exchange.HTTP_METHOD);

        String uri = null;
        String getAllTcUri = this.getTcCirclesURI();
        ArrayList<TrustCircle> tcList = camelRestService.sendTc(getAllTcUri, null,  HttpMethod.GET.name(), TrustCircle.class);
        LOG.info(tcList.toString());
        for (TrustCircle tc: tcList){
            if (getTcDataType(tc.getShortName()).equals(integrationData.getDataType().toString())){
                uri = this.getTcCirclesURI() + "/" + tc.getId();
                break;
            }
        }

        //make all TC calls
//        String uri = this.getTcCirclesURI() + "/" + getTcId(integrationData.getDataType().toString());
        TrustCircle tc = camelRestService.send(uri, null,  HttpMethod.GET.name(), TrustCircle.class);

        //TODO: TrustCircle will return Header("X-CSRFToken", csrfToken) and Header("Authorization",authorization);
        String csrfToken ="TBD"; //TODO
        String authorization ="TBD";//TODO
        Map<String,Object> tcAuthHeaders = new HashMap<>();
        tcAuthHeaders.put("X-CSRFToken",csrfToken);
        tcAuthHeaders.put("Authorization",authorization);

        List<Team> teams = new ArrayList<>();
        //first make all calls to get the teams
        for (String teamId : tc.getTeams()){
            //make call to TC-team
            Team team = camelRestService.send(this.getTcTeamsURI() + "/" + teamId, teamId, HttpMethod.GET.name(), Team.class, tcAuthHeaders);
            teams.add(team);
        }
        //all TC calls have been made up to this point, TEAMS list has been populated

        // Decide the flow
        if(originEndpoint.equals(routes.apply(CamelRoutes.DCL))) {
            for(Team team:teams) {
                //send to ECSP
                LOG.info(team.toString());
                LOG.info(integrationData.toString());
                handleDclFlowAndSendToECSP(httpMethod, team, integrationData);
            }
        }else if(originEndpoint.equals(routes.apply(CamelRoutes.EDCL))){
            handleExternalDclFlowAndSendToDSL(exchange, teams, integrationData);
        }
    }

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

    private String getTcDataType(String dataType){
        String dt = null;
        switch(dataType) {
            case "CTC::SHARING_DATA_THREAT":
                dt = "threat";
                break;
            case "CTC::SHARING_DATA_EVENT":
                dt = "event";
                break;
            case "CTC::SHARING_DATA_ARTEFACT":
                dt = "artefact";
                break;
            case "CTC::SHARING_DATA_INCIDENT":
                dt = "incident";
                break;
            case "CTC::SHARING_DATA_CONTACT":
                dt = "contact";
                break;
            case "CTC::SHARING_DATA_FILE":
                dt = "file";
                break;
            case "CTC::SHARING_DATA_CHAT\n":
                dt = "chat";
                break;
            case "CTC::SHARING_DATA_VULNERABILITY":
                dt = "vulnerability";
                break;
            case "CTC::CSP_ALL":
                dt = "trustCircle";
                break;
        }
        return dt;
    }
}
