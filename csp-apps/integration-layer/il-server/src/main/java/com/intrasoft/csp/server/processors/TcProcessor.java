package com.intrasoft.csp.server.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.anon.client.AnonClient;
import com.intrasoft.csp.anon.commons.exceptions.AnonException;
import com.intrasoft.csp.anon.commons.exceptions.InvalidDataTypeException;
import com.intrasoft.csp.anon.commons.exceptions.MappingNotFoundForGivenTupleException;
import com.intrasoft.csp.anon.commons.model.IntegrationAnonData;
import com.intrasoft.csp.commons.exceptions.ErrorLogException;
import com.intrasoft.csp.commons.exceptions.InvalidSharingParamsException;
import com.intrasoft.csp.commons.model.*;
import com.intrasoft.csp.commons.routes.CamelRoutes;
import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;
import com.intrasoft.csp.server.policy.domain.model.EvaluatedPolicyDTO;
import com.intrasoft.csp.server.policy.domain.model.SharingPolicyAction;
import com.intrasoft.csp.server.policy.service.SharingPolicyService;
import com.intrasoft.csp.server.service.CamelRestService;
import com.intrasoft.csp.server.routes.RouteUtils;
import org.apache.camel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.script.ScriptException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by iskitsas on 4/9/17.
 */
@Component
public class TcProcessor implements Processor,CamelRoutes{
    private static final Logger LOG = LoggerFactory.getLogger(TcProcessor.class);

    @Value("${server.name}")
    String serverName;
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
    SharingPolicyService sharingPolicyService;

    @Autowired
    AnonClient anonClient;

    @Autowired
    RouteUtils routes;

    @PostConstruct
    public void init(){
        LOG.info("This CSP server name is: "+serverName);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String originEndpoint = (String) exchange.getIn().getHeader(CamelRoutes.ORIGIN_ENDPOINT);
        String msg = originEndpoint.equals(routes.apply(CamelRoutes.DCL))? "send to external CSP":
                originEndpoint.equals(routes.apply(CamelRoutes.EDCL))? " handle from external CSP":"";
        LOG.info("DCL - Get Trust Circles from TC API and "+msg+" [ORIGIN_ENDPOINT:"+originEndpoint+"]");

        boolean isFlow1 = originEndpoint.equals(routes.apply(CamelRoutes.DCL))?true:false;
        boolean isFlow2 = originEndpoint.equals(routes.apply(CamelRoutes.EDCL))?true:false;

        IntegrationData integrationData = exchange.getIn().getBody(IntegrationData.class);
        String httpMethod = (String) exchange.getIn().getHeader(Exchange.HTTP_METHOD);


        // SXCSP-185. tcId and teamId logic to be implemented - if both throw exception - Malformed 4xx
        if (!StringUtils.isEmpty(integrationData.getSharingParams().getTcId())
                && !StringUtils.isEmpty(integrationData.getSharingParams().getTeamId())) {
            //DO NOT ACTIVATE GDELIVERY by throwing any exception, just log the error
            throw new InvalidSharingParamsException("Invalid sharing params provided: tcId and team were both provided. " +
                    "Only one or none should be provided. "+integrationData.getSharingParams().toString());
        }

        if(isFlow1) {
            if (integrationData.getSharingParams().getTcId()!=null && integrationData.getSharingParams().getTcId().size()>0) {
                //send by tcId provided - only in flow1
                for(String tcId:integrationData.getSharingParams().getTcId()) {
                    sendByTcId(tcId, exchange);
                }
            } else if (integrationData.getSharingParams().getTeamId()!=null && integrationData.getSharingParams().getTeamId().size()>0) {
                //send by teamId provided - only in flow1
                for(String teamId:integrationData.getSharingParams().getTeamId()) {
                    sendByTeamId(teamId, exchange);
                }
            } else {
                //send by dataType
                sendByDataType(integrationData, exchange, originEndpoint, httpMethod);
            }
        } else if(isFlow2){
            //send by dataType
            sendByDataType(integrationData, exchange, originEndpoint, httpMethod);
        }

    }

    private void sendByDataType(IntegrationData integrationData, Exchange exchange, String originEndpoint, String httpMethod) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        //make all-TCs call
        String getAllTcUri = this.getTcCirclesURI();
        List<TrustCircle> tcList = camelRestService.sendAndGetList(getAllTcUri, null,  HttpMethod.GET.name(), TrustCircle.class,null);

        Optional<TrustCircle> optionalTc  = tcList.stream().filter(t->t.getShortName().toLowerCase().contains(IntegrationDataType.tcNamingConventionForShortName.get(integrationData.getDataType()).toString().toLowerCase())).findAny();
        if(optionalTc.isPresent()){
            sendByTcId(optionalTc.get().getId(),exchange);
        }else{
            //DO NO ACTIVATE GDELIVERY by throwing any exception, just log the error
            throw new ErrorLogException("Could not find trust circle id for this data. "+integrationData.toString());
        }
    }

    private void sendByTcId(String tcId, Exchange exchange) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        String uri = this.getTcCirclesURI() + "/" + tcId;
        List<Team> teams = getTcTeams(uri,exchange);
        //all TC calls have been made up to this point, TEAMS list has been populated
        // Decide the flow
        decideTheFlow(teams,exchange);
    }


    List<Team> getTcTeams(String uri, Exchange exchange) throws IOException {
        String originEndpoint = (String) exchange.getIn().getHeader(CamelRoutes.ORIGIN_ENDPOINT);
        boolean isFlow1 = originEndpoint.equals(routes.apply(CamelRoutes.DCL));
        return  getTcTeamsByArg(uri,isFlow1);
    }

    List<Team> getTcTeamsByArg(String uri, boolean isFlow1) throws IOException {
        TrustCircle tc = camelRestService.send(uri, null,  HttpMethod.GET.name(), TrustCircle.class);
        List<Team> teams = new ArrayList<>();
        //first make all calls to get the teams
        for (String teamId : tc.getTeams()) {
            //make call to TC-team
            Team team = camelRestService.send(this.getTcTeamsURI() + "/" + teamId, teamId, HttpMethod.GET.name(), Team.class);
            if (StringUtils.isEmpty(team.getShortName())) {
                //DO NO ACTIVATE GDELIVERY by throwing any exception, just log the error and let the code flow to next iteration
                LOG.error("Team short name received from TC API is empty/null - SKIPPING this team... \n" +
                        "TrustCircle: " + tc.toString() + "\n" +
                        "Team: " + team.toString());
                continue; // continue to next iteration
            }

            if (StringUtils.isEmpty(team.getCspId())) {
                //DO NO ACTIVATE GDELIVERY by throwing any exception, just log the error and let the code flow to next iteration
                LOG.error("CspId received from TC API is empty/null - SKIPPING this team... \n" +
                        "TrustCircle: " + tc.toString() + "\n" +
                        "Team: " + team.toString());
                continue;// continue to next iteration
            }

            if(isFlow1){
                //the following is only valid for flow1
                //SXCSP-255. Using cspId and not shortName
                if (!team.getCspId().toLowerCase().trim().equals(serverName.toLowerCase().trim())) {
                    teams.add(team);
                }
            }else{
                teams.add(team);
            }
        }
        LOG.trace("-- Teams: "+teams.toString());
        return teams;
    }


    public List<Team> getTeamsByTrustCircleIdFlow1(String tcId) throws IOException {
        String uri = this.getTcCirclesURI() + "/" + tcId;
        return getTcTeamsByArg(uri,true);
    }

    public List<Team> getTcTeamsFlow1(IntegrationDataType integrationDataType) throws IOException {
        String uri = getTcUri(integrationDataType);
        return getTcTeamsByArg(uri,true);
    }

    public List<Team> getAllTcTeams(IntegrationDataType integrationDataType) throws IOException {
        String uri = getTcUri(integrationDataType);
        return getTcTeamsByArg(uri,false);
    }

    public String getTcUri(IntegrationDataType integrationDataType) throws IOException {
        String uri;
        String getAllTcUri = this.getTcCirclesURI();
        List<TrustCircle> tcList = camelRestService.sendAndGetList(getAllTcUri, null,  HttpMethod.GET.name(), TrustCircle.class,null);

        Optional<TrustCircle> optionalTc  = tcList.stream().filter(t->t.getShortName().toLowerCase().contains(IntegrationDataType.tcNamingConventionForShortName.get(integrationDataType).toString().toLowerCase())).findAny();
        if(optionalTc.isPresent()){
            uri = this.getTcCirclesURI() + "/" + optionalTc.get().getId();
        }else{
            //SHOULD NOT activate GDelivery. Log as error
            throw new ErrorLogException("Integration Test error: Could not find trust circle id for this data. ");
        }
        return uri;
    }

    private void sendByTeamId(String teamId, Exchange exchange) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        Team team = getTeamByRestCall(teamId);
        List<Team> teams = new ArrayList<>();
        //SXCSP-255. Using cspId and not shortName
        if (!team.getCspId().toLowerCase().trim().equals(serverName.toLowerCase().trim())){
            teams.add(team);
            decideTheFlow(teams,exchange);
        }else{
            LOG.warn("Sending by team id("+teamId+") to itself(serverName: "+serverName.toLowerCase().trim()+") is not " +
                    "supported - Nothing to send.");
        }
    }

    private void decideTheFlow(List<Team> teams, Exchange exchange) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        String originEndpoint = (String) exchange.getIn().getHeader(CamelRoutes.ORIGIN_ENDPOINT);
        IntegrationData integrationData = exchange.getIn().getBody(IntegrationData.class);
        String httpMethod = (String) exchange.getIn().getHeader(Exchange.HTTP_METHOD);
        // Decide the flow
        if(originEndpoint.equals(routes.apply(CamelRoutes.DCL))) {//flow1
            for(Team t:teams) {
                //send to ECSP
                LOG.trace(t.toString());
                LOG.trace(integrationData.toString());
                handleDclFlowAndSendToECSP(httpMethod, t, integrationData);
            }
        }else if(originEndpoint.equals(routes.apply(CamelRoutes.EDCL))){//flow2
            handleExternalDclFlowAndSendToDSL(exchange,httpMethod, teams, integrationData);
        }
    }

    public Team getTeamByRestCall(String teamId) throws IOException {
        Team team = camelRestService.send(this.getTcTeamsURI() + "/" + teamId, teamId, HttpMethod.GET.name(), Team.class);
        if(team.getShortName()==null){
            //SHOULD NOT activate GDelivery
            throw new ErrorLogException("Team short name received from TC API is null - cannot proceed. \n" +
                    "Team: "+team.toString());
        }
        if(team.getCspId()==null){
            //SHOULD NOT activate GDelivery
            throw new ErrorLogException("CspId received from TC API is null - cannot proceed. \n" +
                    "Team: "+team.toString());
        }
        return team;
    }

    //flow1
    private void handleDclFlowAndSendToECSP(String httpMethod, Team team, IntegrationData integrationData) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        // SXCSP-85 Sharing Policy to be integrated only when sending to ECSP; TrustCircle should be excluded
        SharingPolicyAction sharingPolicyAction = SharingPolicyAction.SHARE_AS_IS;
        /**
         * 3 actions:
         * -share as is
         * -share anonymized
         * -dont share
         */
        if(!integrationData.getDataType().equals(IntegrationDataType.TRUSTCIRCLE)) {
            // EVALUATE ONLY IF DATA_TYPE IS NOT TRUSTCIRCLE
            EvaluatedPolicyDTO evaluatedPolicyDTO = sharingPolicyService.evaluate(integrationData, team);
            sharingPolicyAction = evaluatedPolicyDTO.getSharingPolicyAction();
        }
        switch (sharingPolicyAction){
            case SHARE_AS_IS:
                break;
            case SHARE_ANONYMIZED:
                IntegrationAnonData integrationAnonData = new IntegrationAnonData();
                integrationAnonData.setCspId(team.getCspId());
                integrationAnonData.setDataType(integrationData.getDataType());
                integrationAnonData.setDataObject(integrationData.getDataObject());
                IntegrationAnonData anonData = null;
                try {
                    anonData = anonClient.postAnonData(integrationAnonData);
                } catch (InvalidDataTypeException|NoSuchAlgorithmException|InvalidKeyException|IOException|AnonException|MappingNotFoundForGivenTupleException e) {
                    LOG.error("Could not anonymize, falling back to 'DO_NOT_SHARE'. "+integrationAnonData.toString(),e);
                    return;
                }
                integrationData.setDataObject(anonData.getDataObject());
                String json = objectMapper.writeValueAsString(integrationData);
                LOG.trace("---- Anonymized json: \n\n"+json);
                break;
            case DO_NOT_SHARE:
                return;
            case NO_ACTION_FOUND:
                break;
        }
        EnhancedTeamDTO enhancedTeamDTO = new EnhancedTeamDTO(team, integrationData);
        Map<String, Object> headers = new HashMap<>();

        headers.put(Exchange.HTTP_METHOD, httpMethod);
        producer.sendBodyAndHeaders(routes.apply(ECSP), ExchangePattern.InOut, enhancedTeamDTO, headers);
    }

    // flow2
    private void handleExternalDclFlowAndSendToDSL(Exchange exchange,String httpMethod,List<Team> teams, IntegrationData integrationData){

        //SXCSP-255. Using cspId and not shortName
        //should have all teams regardless of any teamId provided in sharingParams
        boolean authorized = teams.stream().anyMatch(t->t.getCspId().toLowerCase().equals(integrationData.getDataParams().getCspId().toLowerCase()));
        LOG.info("Authorized (cspId or shortName="+integrationData.getDataParams().getCspId().toLowerCase()+"): "+authorized);
        if (authorized){
            integrationData.getSharingParams().setIsExternal(true);

            //exchange.getIn().setBody(integrationData); //replace with producer
            //exchange.getIn().setHeader("recipients", routes.apply(DSL));//replace with producer
            Map<String, Object> headers = new HashMap<>();
            headers.put(Exchange.HTTP_METHOD, httpMethod);
            producer.sendBodyAndHeaders(routes.apply(DSL),ExchangePattern.InOut,integrationData,headers);
        }
    }

    private String getTcCirclesURI() {
        return tcProtocol + "://" + tcHost + ":" + tcPort + tcPathCircles;
    }
    private String getTcTeamsURI() {
        return tcProtocol + "://" + tcHost + ":" + tcPort + tcPathTeams;
    }
}
