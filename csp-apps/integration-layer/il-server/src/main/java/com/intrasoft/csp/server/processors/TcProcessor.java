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
import com.intrasoft.csp.server.routes.RouteUtils;
import com.intrasoft.csp.server.service.CamelRestService;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

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

    @Value("${tc.path.localcircle}")
    String tcPathLocalCircle;

    @Value("${server.camel.rest.service.is.async:true}")
    Boolean camelRestServiceIsAsync;

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
        String msg = originEndpoint.equals(routes.wrap(CamelRoutes.DCL))? "send to external CSP":
                originEndpoint.equals(routes.wrap(CamelRoutes.EDCL))? " handle from external CSP":"";
        LOG.debug("DCL - Get Trust Circles from TC API and "+msg+" [ORIGIN_ENDPOINT:"+originEndpoint+"]");

        boolean isFlow1 = originEndpoint.equals(routes.wrap(CamelRoutes.DCL))?true:false;
        boolean isFlow2 = originEndpoint.equals(routes.wrap(CamelRoutes.EDCL))?true:false;
        LOG.debug("flow 1 = {}, flow 2 = {}", isFlow1, isFlow2);
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
            if (integrationData.getSharingParams().getTcId()!=null) {
                LOG.debug("F1 tc Id {}", integrationData.getSharingParams().getTcId());
                //send by tcId provided - only in flow1
                if(integrationData.getSharingParams().getTcId() instanceof List){
                    List<String> list = (List<String>) integrationData.getSharingParams().getTcId();
                    if(list.size()>0){
                        LOG.debug("F1 Sending to list {}", list);
                        for(String tcId:list) {
                            if(!StringUtils.isEmpty(tcId)) {
                                LOG.debug("F1 sending to {}", tcId);
                                sendByTcId(tcId,localTcExists(tcId), exchange);
                            }
                        }
                    }
                }else if(integrationData.getSharingParams().getTcId() instanceof String){
                    String tcId = (String) integrationData.getSharingParams().getTcId();
                    LOG.debug("F1 single TC: {}",tcId);
                    if(!StringUtils.isEmpty(tcId)) {
                        sendByTcId(tcId, localTcExists(tcId),exchange);
                    }
                }

            } else if (integrationData.getSharingParams().getTeamId()!=null) {
                //send by teamId provided - only in flow1
                LOG.debug("F1 TeamId found - {}", integrationData.getSharingParams().getTeamId());
                if(integrationData.getSharingParams().getTeamId() instanceof List){
                    List<String> list = (List<String>) integrationData.getSharingParams().getTeamId();
                    if(list.size()>0){
                        LOG.debug("F1 list teamId: {}", integrationData.getSharingParams().getTeamId());

                        for(String teamId:list) {
                            if(!StringUtils.isEmpty(teamId)) {
                                LOG.debug("F1.1 sending to {}", teamId);
                                sendByTeamId(teamId, exchange);
                            }
                        }
                    }
                }else if(integrationData.getSharingParams().getTeamId() instanceof String){
                    LOG.debug("F1 single teamId: {}", integrationData.getSharingParams().getTeamId());

                    String teamId = (String) integrationData.getSharingParams().getTeamId();
                    if(!StringUtils.isEmpty(teamId)) {
                        LOG.debug("F1.2 sending to {}", teamId);

                        sendByTeamId(teamId, exchange);
                    }
                }
            } else {
                LOG.debug("F1 not teamId or tcId - sending by datatype {}", integrationData.getDataType());
                //send by dataType
                sendByDataType(integrationData, exchange, originEndpoint, httpMethod);
            }
        } else if(isFlow2){
            //send by dataType
            sendByDataType(integrationData, exchange, originEndpoint, httpMethod);
        }

    }

    private void sendByDataType(IntegrationData integrationData, Exchange exchange, String originEndpoint, String httpMethod) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        Optional<TrustCircle> optionalTc = Optional.empty();
        List<TrustCircle> tcList = null;
        String tcShortNameMapping = IntegrationDataType.CTC_CSP_SHARING;

        if(!integrationData.getDataType().equals(IntegrationDataType.TRUSTCIRCLE)) {
            //first make an LTC call and if available use that, instead of CTC.
            String getAllLocalTcUri = this.getLocalCirclesURI() + "?short_name=" + IntegrationDataType.LTC_CSP_SHARING;
            tcList = camelRestService.sendAndGetList(getAllLocalTcUri, null, HttpMethod.GET.name(), TrustCircle.class, null, true);
        }else{
            tcShortNameMapping = IntegrationDataType.CTC_CSP_ALL;
        }

        boolean useLTC = false;
        if(tcList == null || tcList.isEmpty()) {
            LOG.info("Using "+tcShortNameMapping+"..");
            //make all-TCs call
            String getAllTcUri = this.getTcCirclesURI();

            // there is a case that CTC_CSP_ALL does not exist anymore
            try {
                TrustCircle tc = camelRestService.send(getAllTcUri + "/" + tcShortNameMapping, null, HttpMethod.GET.name(), TrustCircle.class, null);
                optionalTc = Optional.of(tc);
            } catch (CspBusinessException cbe) {
                LOG.warn("Exception "+cbe.getMessage()+ " when trying to get "+tcShortNameMapping);
                if (IntegrationDataType.CTC_CSP_ALL.equalsIgnoreCase(tcShortNameMapping) &&
                    integrationData.getDataType().equals(IntegrationDataType.TRUSTCIRCLE)) {
                    //special case, trustcircle update and CTC::CSP_ALL does not exist
                    LOG.warn("Trying to pass CTC::CSP_ALL update through...");
                    sendByTcId(null, useLTC, exchange);
                    return;
                }
            }
           }else{
            LOG.info("Using "+IntegrationDataType.LTC_CSP_SHARING+"..");
            optionalTc  = tcList.stream().findAny();
            useLTC = true;
        }

        if(optionalTc.isPresent()){
            sendByTcId(optionalTc.get().getId(),useLTC, exchange);
        }else{
            //DO NO ACTIVATE GDELIVERY by throwing any exception, just log the error
            throw new ErrorLogException("Could not find trust circle id for this data. "+integrationData.toString());
        }
    }


    boolean localTcExists(String uuid) throws IOException {
        return tcExists(uuid,true);
    }

    boolean centralTcExists(String uuid) throws IOException {
        return tcExists(uuid,false);
    }

    boolean tcExists(String uuid, boolean isLocal) throws IOException {
        LOG.debug("tcExists: Checking if {} exists (isLocal={})", uuid, isLocal);
        String uri = (isLocal?this.getLocalCirclesURI():this.getTcCirclesURI()) + "/" + uuid;
        //handle "404 not found" properly
        List<Integer> doNotLogErrorOnTheseStatusCodes = new ArrayList<>();
        doNotLogErrorOnTheseStatusCodes.add(404);
        TrustCircle tc = camelRestService.send(uri, null,  HttpMethod.GET.name(),
                TrustCircle.class,true, doNotLogErrorOnTheseStatusCodes);
        LOG.debug("tcExists: {} result is {} (found {})", uuid, tc != null, tc);
        return tc != null;
    }

    private void sendByTcId(String tcId, boolean useLTC, Exchange exchange) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        List<Team> teams = new ArrayList<>();
        if (tcId != null) {
            String uri = (useLTC ? this.getLocalCirclesURI() : this.getTcCirclesURI()) + "/" + tcId;
            teams.addAll(getTcTeams(uri, exchange));
        }
        //all TC calls have been made up to this point, TEAMS list has been populated
        // Decide the flow
        decideTheFlow(teams,exchange);
    }


    List<Team> getTcTeams(String uri, Exchange exchange) throws IOException {
        String originEndpoint = (String) exchange.getIn().getHeader(CamelRoutes.ORIGIN_ENDPOINT);
        boolean isFlow1 = originEndpoint.equals(routes.wrap(CamelRoutes.DCL));
        return  getTcTeamsByArg(uri,isFlow1);
    }

    List<Team> getTcTeamsByArg(String uri, boolean isFlow1) throws IOException {
        TrustCircle tc = camelRestService.send(uri, null,  HttpMethod.GET.name(), TrustCircle.class);
        LOG.debug("Received TC {}(size={}) from {}, flow is {} - deciding teams ",
                tc.getId(),tc.getTeams().size(), uri, isFlow1 ? "flow1" : "flow2");
        List<Team> teams = new ArrayList<>();
        //first make all calls to get the teams
        for (String teamId : tc.getTeams()) {
            //make call to TC-team
            Team team = camelRestService.send(this.getTcTeamsURI() + "/" + teamId, teamId, HttpMethod.GET.name(), Team.class);
            if (StringUtils.isEmpty(team.getShortName())) {
                //DO NO ACTIVATE GDELIVERY by throwing any exception, just log the error and let the code flow to next iteration
                LOG.error("Team short name received from TC API is empty/null - SKIPPING this team... TrustCircle: {} Team: {}" , tc,  team);
                continue; // continue to next iteration
            }

            if (StringUtils.isEmpty(team.getCspId())) {
                //DO NO ACTIVATE GDELIVERY by throwing any exception, just log the error and let the code flow to next iteration
                LOG.error("Team id received from TC API is empty/null - SKIPPING this team... TrustCircle: {} Team: {}" , tc,  team);
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
        LOG.debug("--> Decided: (size={}) {}",teams.size(), printableTeamsList(teams));
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
        Optional<TrustCircle> optionalTc;
        String uri;
        List<TrustCircle> tcList = null;
        String tcShortNameMapping = IntegrationDataType.CTC_CSP_SHARING;

        if(!integrationDataType.equals(IntegrationDataType.TRUSTCIRCLE)) {
            //if trustCircle, do not check LTC
            String getAllLocalTcUri = this.getLocalCirclesURI() + "?short_name=" + IntegrationDataType.LTC_CSP_SHARING;
            tcList = camelRestService.sendAndGetList(getAllLocalTcUri, null, HttpMethod.GET.name(), TrustCircle.class, null);
        }else{
            tcShortNameMapping = IntegrationDataType.CTC_CSP_ALL;
        }

        if(tcList == null || tcList.isEmpty()) {
            LOG.info("Using "+tcShortNameMapping+"..");
            String getAllTcUri = this.getTcCirclesURI();
            //we can get a TC by short name. It is already supported!
            //eg.
            //tcList = camelRestService.sendAndGetList(getAllTcUri+"/"+tcShortNameMapping, null, HttpMethod.GET.name(), TrustCircle.class, null);
            //optionalTc  = tcList.stream().findAny();
            TrustCircle tc = camelRestService.send(getAllTcUri+"/"+tcShortNameMapping, null, HttpMethod.GET.name(), TrustCircle.class, null);
            optionalTc = Optional.of(tc);
            //tcList = camelRestService.sendAndGetList(getAllTcUri, null,  HttpMethod.GET.name(), TrustCircle.class,null);
            //optionalTc = tcList.stream().filter(t->t.getShortName().toLowerCase().contains(IntegrationDataType.tcNamingConventionForShortName.get(integrationDataType).toString().toLowerCase())).findAny();
            uri = this.getTcCirclesURI();
        }else{
            LOG.info("Using "+IntegrationDataType.LTC_CSP_SHARING+"..");
            optionalTc = tcList.stream().findAny();
            uri = this.getLocalCirclesURI();
        }

        if(optionalTc.isPresent()){
            uri = uri + "/" + optionalTc.get().getId();
        }else{
            //SHOULD NOT activate GDelivery. Log as error
            throw new ErrorLogException("Integration Test error: Could not find trust circle id for this data. ");
        }
        LOG.debug("===> TC API uri {} [optionalTC = {}]",uri, optionalTc.orElse(null));
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
        if(originEndpoint.equals(routes.wrap(CamelRoutes.DCL))) {//flow1
            LOG.debug("[flow1] == {} will send to (count={}) {}",originEndpoint, teams.size(), printableTeamsList(teams));
            for(Team t:teams) {
                //send to ECSP
                handleDclFlowAndSendToECSP(httpMethod, t, integrationData);
            }
        }else if(originEndpoint.equals(routes.wrap(CamelRoutes.EDCL))){//flow2
            LOG.debug("[flow2] == {} will send to (count={}) {}",originEndpoint,teams.size(), printableTeamsList(teams));
            handleExternalDclFlowAndSendToDSL(httpMethod, teams, integrationData);
        }
    }

    private List<String> printableTeamsList(List<Team> teams) {
        return teams.stream().map(t -> {
            return String.format("[cspId:%s,short:%s]", t.getCspId(), t.getShortName());
        }).collect(Collectors.toList());
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
        LOG.debug("Team received cspId={},short={},id={}", team.getCspId(), team.getShortName(), team.getId());
        return team;
    }

    //flow1
    private void handleDclFlowAndSendToECSP(String httpMethod, Team team, IntegrationData coreIntegrationData) throws NoSuchAlgorithmException, InvalidKeyException, IOException {

        //[fix] SXCSP-431: clone the integrationData object to avoid any changes by reference when anonymizing
        String jsonIntegrationData = objectMapper.writeValueAsString(coreIntegrationData);
        IntegrationData integrationData = objectMapper.readValue(jsonIntegrationData, IntegrationData.class);

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
            case SHARE_ANONYMIZED:
                IntegrationAnonData integrationAnonData = new IntegrationAnonData();
                integrationAnonData.setCspId(team.getCspId());
                integrationAnonData.setDataType(integrationData.getDataType());
                integrationAnonData.setDataObject(integrationData.getDataObject());
                integrationAnonData.setApplicationId(integrationData.getDataParams().getApplicationId());
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
            case SHARE_AS_IS:
            case NO_ACTION_FOUND:
                break;
        }
        EnhancedTeamDTO enhancedTeamDTO = new EnhancedTeamDTO(team, integrationData);
        Map<String, Object> headers = new HashMap<>();

        headers.put(Exchange.HTTP_METHOD, httpMethod);
        if (!camelRestServiceIsAsync) {
            producer.sendBodyAndHeaders(routes.wrap(ECSP+"."+ routes.safeQueueName(enhancedTeamDTO.getTeam())),
                    ExchangePattern.InOnly, enhancedTeamDTO, headers);//TODO: investigate SXCSP-430 - do we need inOut here?
        } else {
            camelRestService.asyncSendInOnly(routes.wrap(ECSP+"."+ routes.safeQueueName(enhancedTeamDTO.getTeam())),
                    enhancedTeamDTO, headers);
        }
        // now we send to the notifier queue, that messages for team X are pending.
        // the processor will receive the queue name, and proceed to verify the team connectivity and then dispatch
        producer.sendBodyAndHeaders(routes.wrap(CamelRoutes.NOTIFIER),
                ExchangePattern.InOnly, enhancedTeamDTO.getTeam(), headers);

    }

    // flow2
    private void handleExternalDclFlowAndSendToDSL(String httpMethod,List<Team> teams, IntegrationData coreIntegrationData)
            throws IOException {
        String jsonIntegrationData = objectMapper.writeValueAsString(coreIntegrationData);
        IntegrationData integrationData = objectMapper.readValue(jsonIntegrationData, IntegrationData.class);

        final List<String> authorizedCentralCspIdsList = Arrays.asList(IntegrationDataType.authorizedCentralCspIds);
        final String cspId = integrationData.getDataParams().getCspId();

        // 1. SXCSP-255. Using cspId and not shortName (fixed)
        // 2. https://github.com/melicertes/csp/issues/60 (fixed)
        //  short story - catch-22 central wants to send out a nuke and rewrite local CTC entries
        //  to do so, previous code was expecting that CTC::CSP_ALL contains 'central' but central was wrong on TC app.
        //  Test below fails when locals are asked to remove central from their list (so to solve the TC app issue).
        //  New code below authorizes as per previous (no change for other data types) but adds a secondary check to
        //  authorise the operation if the incoming integrationData come from 'central' or 'central-csp' and the
        //  datatype is TRUSTCIRCLE. This will potentially allow a nuke operation to go through, assuming that
        //  the trustcircle CTC::CSP_ALL exists
        boolean authorized = teams.stream().anyMatch(t->t.getCspId().equalsIgnoreCase(cspId))
                || ( authorizedCentralCspIdsList.stream().anyMatch(c->c.equalsIgnoreCase(cspId)) &&
                     integrationData.getDataType().equals(IntegrationDataType.TRUSTCIRCLE));

        LOG.info("(flow2) cspId "+ cspId +" -> authorized = "+authorized);
        if (authorized){
            integrationData.getSharingParams().setIsExternal(true);
            boolean shouldSend = true;

            if(integrationData.getDataType().equals(IntegrationDataType.TRUSTCIRCLE)
                    && !authorizedCentralCspIdsList.stream().anyMatch(c->c.equalsIgnoreCase(cspId))){
                shouldSend = false;
                LOG.error(String.format("Rejecting TC dataType change from external CSP %s (flow2), not in %s. ",
                        cspId, authorizedCentralCspIdsList.toString()));
            }

            if(shouldSend) {
                Map<String, Object> headers = new HashMap<>();
                headers.put(Exchange.HTTP_METHOD, httpMethod);
                if (!camelRestServiceIsAsync) {
                    producer.sendBodyAndHeaders(routes.wrap(DSL), ExchangePattern.InOnly, integrationData, headers);
                } else {
                    camelRestService.asyncSendInOnly(routes.wrap(DSL), integrationData, headers);
                }
            }
        }
    }

    public String getLocalCirclesURI() {
        return tcProtocol + "://" + tcHost + ":" + tcPort + tcPathLocalCircle;
    }

    public String getTcCirclesURI() {
        return tcProtocol + "://" + tcHost + ":" + tcPort + tcPathCircles;
    }
    public String getTcTeamsURI() {
        return tcProtocol + "://" + tcHost + ":" + tcPort + tcPathTeams;
    }
}
