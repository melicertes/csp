package com.intrasoft.csp.server.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.commons.apiHttpStatusResponse.HttpStatusResponseType;
import com.intrasoft.csp.commons.model.*;
import com.intrasoft.csp.commons.model.elastic.ElasticSearchResponse;
import com.intrasoft.csp.commons.model.elastic.search.Hit;
import com.intrasoft.csp.commons.model.elastic.search.Hits;
import com.intrasoft.csp.commons.routes.ContextUrl;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spring.SpringCamelContext;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by iskitsas on 4/10/17.
 */
@Component
public class MockUtils implements ContextUrl {
    private static final Logger LOG = LoggerFactory.getLogger(MockUtils.class);

    SpringCamelContext springCamelContext;

    @Autowired
    ObjectMapper objectMapper;

    String dataObjectToTest = "{\n" +
            "\"xml_advisory\": {\n" +
            "    \"meta_info\": {\n" +
            "      \"system_information\": {\n" +
            "        \"systemdetail\": {\n" +
            "          \"affected_products_text\": \"Apple iTunes\",\n" +
            "          \"affected_platform\": {\n" +
            "            \"platform\": {\n" +
            "              \"producer\": \"Microsoft\",\n" +
            "              \"name\": \"Windows\",\n" +
            "              \"version\": \"10\"\n" +
            "            }\n" +
            "          },\n" +
            "          \"affected_products_versions_text\": \"11\",\n" +
            "          \"affected_platforms_text\": \"Microsoft Windows 10\",\n" +
            "          \"affected_product\": {\n" +
            "            \"product\": {\n" +
            "              \"producer\": \"Apple\",\n" +
            "              \"name\": \"iTunes\",\n" +
            "              \"version\": \"11\"\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"probability\": \"high\",\n" +
            "      \"version_history\": {\n" +
            "        \"version_instance\": { \"version\": \"1.00\" }\n" +
            "      },\n" +
            "      \"title\": \"Advisory regarding recent Taranis 3.3.3 vulnerability\",\n" +
            "      \"taranis_version\": \"3.0\",\n" +
            "      \"vulnerability_effect\": {\n" +
            "         \n" +
            "      },\n" +
            "      \"damage\": \"high\",\n" +
            "      \"issuer\": \"Taranis\",\n" +
            "      \"availability\": \"https://kennisbank.ncsc.nl/\",\n" +
            "      \"reference_number\": \"Taranis-2017-XXXX\",\n" +
            "      \"vulnerability_identifiers\": {\n" +
            "        \"cve\": { \"id\": \"CVE-2017-7578\" }\n" +
            "      }\n" +
            "    },\n" +
            "    \"rating\": {\n" +
            "      \"publisher_analysis\": {\n" +
            "        \"ques_pro_expect\": \"3\",\n" +
            "        \"ques_pro_exploit\": \"6\",\n" +
            "        \"ques_pro_complexity\": \"3\",\n" +
            "        \"ques_pro_access\": \"6\",\n" +
            "        \"ques_pro_details\": \"2\",\n" +
            "        \"ques_pro_solution\": \"3\",\n" +
            "        \"ques_dmg_privesc\": \"1\",\n" +
            "        \"ques_pro_standard\": \"3\",\n" +
            "        \"ques_dmg_infoleak\": \"2\",\n" +
            "        \"ques_pro_credent\": \"2\",\n" +
            "        \"ques_pro_userint\": \"3\",\n" +
            "        \"ques_dmg_remrights\": \"1\",\n" +
            "        \"ques_pro_exploited\": \"3\",\n" +
            "        \"ques_dmg_dos\": \"1\",\n" +
            "        \"ques_dmg_codeexec\": \"2\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"content\": {\n" +
            "      \"additional_resources\": { \"resource\": \"http://cve.mitre.org/cve/\" },\n" +
            "      \"solution\": \"Uninstall iTunes, Taranis and MacOS.\",\n" +
            "      \"disclaimer\": \"Door gebruik van deze security advisory gaat u akkoord met de navolgende voorwaarden.\",\n" +
            "      \"abstract\": \"Taranis 3.3.3 when combined with Itunes 11 under Windows 10 cause the Safari browser to crush.\",\n" +
            "      \"consequences\": \"The computer may freeze.\",\n" +
            "      \"description\": \"Taranis 3.3.3 when combined with Itunes 11 under Windows 10 cause the Safari browser to crush.\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

    /**
     * examples: getMockedTrustCircle(3, "http://external.csp%s.com")
     * */
    public TrustCircle getMockedTrustCircle(int count){
        TrustCircle trustCircle = new TrustCircle();
        trustCircle.setId("dummyId");
        trustCircle.setShortName("CTC::SHARING_DATA_INCIDENT");
        //List<String> listCsps = new ArrayList<>();
        List<String> teamList = new ArrayList<>();
        for(int i=0; i< count;i++) {
            //listCsps.add(String.format(strWithCountArg,""+(i+1)));
            teamList.add(i+"");
        }
        trustCircle.setTeams(teamList);
        return trustCircle;
    }

    public List<TrustCircle> getAllMockedTrustCircles(int count){
        List<TrustCircle> ret= new ArrayList<>();
        ret.add(getMockedTrustCircle(count));
        return ret;
    }


    public TrustCircle getMockedTrustCircle(int count, String shortName){
        TrustCircle trustCircle = new TrustCircle();
        trustCircle.setId("dummyId");
        trustCircle.setShortName(shortName);
        //List<String> listCsps = new ArrayList<>();
        List<String> teamList = new ArrayList<>();
        for(int i=0; i< count;i++) {
            //listCsps.add(String.format(strWithCountArg,""+(i+1)));
            teamList.add(i+"");
        }
        trustCircle.setTeams(teamList);
        return trustCircle;
    }

    public List<TrustCircle> getAllMockedTrustCircles(int count, String shortName){
        List<TrustCircle> ret= new ArrayList<>();
        ret.add(getMockedTrustCircle(count, shortName));
        return ret;
    }


    public List<Team> getMockedTeams(int count, String strWithCountArg){
        List<Team> ret = new ArrayList<>();
        for(int i=0; i< count;i++) {
            Team t= new Team();
            t.setUrl(String.format(strWithCountArg,""+(i+1)));
            t.setShortName("sname"+i);
            ret.add(t);
        }
        return ret;
    }

    public Team getMockedTeam(int id, String strWithCountArg) {
        Team team = new Team();
        team.setUrl(String.format(strWithCountArg, "" + id));
        team.setShortName("sname"+id);

        return team;
    }

    public Team getMockedTeam(int id, String strWithCountArg, String shortName) {
        Team team = new Team();
        team.setUrl(String.format(strWithCountArg, "" + id));
        team.setShortName(shortName);

        return team;
    }

    public String getMockedElasticSearchResponse(int countHits) throws JsonProcessingException {
        ElasticSearchResponse elasticSearchResponse = new ElasticSearchResponse();
        Hits hits = new Hits();
        List<Hit> hitList = new ArrayList<>();
        for(int i=0;i<countHits;i++){
            Hit hit = new Hit();
            hit.setId("hit id "+(i+1));
            hitList.add(hit);
        }
        hits.setHits(hitList);
        elasticSearchResponse.setHits(hits);

        return objectMapper.writeValueAsString(elasticSearchResponse);
    }

    /*@Deprecated
    public TrustCircle getMockedTrustCircle(String... mockedECsp){
        TrustCircle trustCircle = new TrustCircle();
        List<String> listCsps = new ArrayList<>();
        for(String s:mockedECsp) {
            listCsps.add(s);
        }
        trustCircle.setCsps(listCsps);
        return trustCircle;
    }*/

    public void sendFlow1IntegrationData(MockMvc mvc, Boolean isExternal) throws Exception {
        sendFlow1IntegrationData(mvc,isExternal, null, null);
    }

    public void sendFlow1IntegrationData(MockMvc mvc, Boolean isExternal, String tcId, String teamId) throws Exception {
        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataType(IntegrationDataType.INCIDENT);

        DataParams dataParams = new DataParams();
        dataParams.setApplicationId("test1");
        dataParams.setCspId("testCspId");
        dataParams.setRecordId("recordId");
        dataParams.setOriginCspId("origin-testCspId");
        dataParams.setOriginApplicationId("origin-test1");
        dataParams.setOriginRecordId("origin-recordId");
        dataParams.setDateTime(DateTime.now());
        dataParams.setUrl("http://rt.cert-gr.melecertes.eu/Ticket/Display.html?id=23453");
        SharingParams sharingParams = new SharingParams();
        sharingParams.setIsExternal(isExternal);
        sharingParams.setToShare(true);
        if(!StringUtils.isEmpty(tcId)){
            sharingParams.setTcId(tcId);
        }
        if(!StringUtils.isEmpty(teamId)){
            sharingParams.setTeamId(teamId);
        }
        integrationData.setDataParams(dataParams);
        integrationData.setDataObject(dataObjectToTest);
        integrationData.setSharingParams(sharingParams);
        mvc.perform(post("/v"+REST_API_V1+"/"+DSL_INTEGRATION_DATA).accept(MediaType.TEXT_PLAIN)
                .content(TestUtil.convertObjectToJsonBytes(integrationData))
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().string(HttpStatusResponseType.SUCCESSFUL_OPERATION.getReasonPhrase()));
    }



    /**
     * Will be deleted in the future. Use the one in which you pass the applicationId argument
     * */
    @Deprecated
    public void sendFlow1Data(MockMvc mvc,String cspId, Boolean isExternal, Boolean toShare, IntegrationDataType dataType, String httpMethod) throws Exception {
        sendFlow1Data(mvc,cspId,"taranis", isExternal,toShare,dataType,httpMethod);
    }

    public void sendFlow1Data(MockMvc mvc, String cspId, String applicationId, Boolean isExternal, Boolean toShare, IntegrationDataType dataType, String httpMethod) throws Exception {
        sendFlow1Data(mvc,cspId,applicationId,null,null,isExternal,toShare,dataType,httpMethod);
    }

    public void sendFlow1Data(MockMvc mvc, String cspId, String applicationId, String tcId, String teamId,
                              Boolean isExternal, Boolean toShare, IntegrationDataType dataType, String httpMethod) throws Exception {
        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataType(dataType);
        SharingParams sharingParams = new SharingParams();
        sharingParams.setIsExternal(isExternal);
        sharingParams.setToShare(toShare);
        if(!StringUtils.isEmpty(tcId)){
            sharingParams.setTcId(tcId);
        }
        if(!StringUtils.isEmpty(teamId)){
            sharingParams.setTeamId(teamId);
        }
        integrationData.setSharingParams(sharingParams);
        DataParams dataParams = new DataParams();
        dataParams.setRecordId("222");
        dataParams.setDateTime(DateTime.now());
        dataParams.setApplicationId(applicationId);
        dataParams.setCspId(cspId);
        dataParams.setOriginCspId("origin-"+cspId);
        dataParams.setOriginApplicationId("origin-"+applicationId);
        dataParams.setOriginRecordId("origin-222");
        dataParams.setUrl("https://rt.cert-gr.melecertes.eu/Ticket/Display.html?id=23453");
        integrationData.setDataParams(dataParams);
        integrationData.setDataObject(dataObjectToTest);


        if (httpMethod.toLowerCase().equals("post")) {
            mvc.perform(post("/v"+REST_API_V1+"/"+DSL_INTEGRATION_DATA).accept(MediaType.TEXT_PLAIN)
                    .content(TestUtil.convertObjectToJsonBytes(integrationData))
                    .contentType(TestUtil.APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andExpect(content().string(HttpStatusResponseType.SUCCESSFUL_OPERATION.getReasonPhrase()));
        }
        else if (httpMethod.toLowerCase().equals("put")) {
            mvc.perform(put("/v"+REST_API_V1+"/"+DSL_INTEGRATION_DATA).accept(MediaType.TEXT_PLAIN)
                    .content(TestUtil.convertObjectToJsonBytes(integrationData))
                    .contentType(TestUtil.APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andExpect(content().string(HttpStatusResponseType.SUCCESSFUL_OPERATION.getReasonPhrase()));
        }
        else if (httpMethod.toLowerCase().equals("delete")) {
            mvc.perform(delete("/v"+REST_API_V1+"/"+DSL_INTEGRATION_DATA).accept(MediaType.TEXT_PLAIN)
                    .content(TestUtil.convertObjectToJsonBytes(integrationData))
                    .contentType(TestUtil.APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andExpect(content().string(HttpStatusResponseType.SUCCESSFUL_OPERATION.getReasonPhrase()));
        }
    }


    public void sendFlow2Data(MockMvc mvc, String applicationId, Boolean isExternal, Boolean toShare, String cspId,
                              IntegrationDataType dataType, String httpMethod) throws Exception {
        sendFlow2Data(mvc,applicationId,null,null,isExternal,toShare,cspId,dataType,httpMethod);
    }

    public void sendFlow2Data(MockMvc mvc, String applicationId,String tcId,String teamId, Boolean isExternal, Boolean toShare, String cspId, IntegrationDataType dataType, String httpMethod) throws Exception {
        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataType(dataType);
        SharingParams sharingParams = new SharingParams();
        sharingParams.setIsExternal(isExternal);
        sharingParams.setToShare(toShare);
        if(!StringUtils.isEmpty(tcId)){
            sharingParams.setTcId(tcId);
        }
        if(!StringUtils.isEmpty(teamId)){
            sharingParams.setTeamId(teamId);
        }
        integrationData.setSharingParams(sharingParams);
        DataParams dataParams = new DataParams();
        dataParams.setRecordId("222");
        dataParams.setApplicationId(applicationId);
        dataParams.setOriginCspId("origin-"+cspId);
        dataParams.setOriginApplicationId("origin-"+applicationId);
        dataParams.setOriginRecordId("origin-222");
        dataParams.setUrl("http://rt.cert-gr.melecertes.eu/Ticket/Display.html?id=23453");
        dataParams.setDateTime(DateTime.now());
        dataParams.setCspId(cspId);
        integrationData.setDataParams(dataParams);
        integrationData.setDataObject(dataObjectToTest);


        if (httpMethod.toLowerCase().equals("post")) {
            mvc.perform(post("/v"+REST_API_V1+"/"+DCL_INTEGRATION_DATA).accept(MediaType.TEXT_PLAIN)
                    .content(TestUtil.convertObjectToJsonBytes(integrationData))
                    .contentType(TestUtil.APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andExpect(content().string(HttpStatusResponseType.SUCCESSFUL_OPERATION.getReasonPhrase()));
        }
        else if (httpMethod.toLowerCase().equals("put")) {
            mvc.perform(put("/v"+REST_API_V1+"/"+DCL_INTEGRATION_DATA).accept(MediaType.TEXT_PLAIN)
                    .content(TestUtil.convertObjectToJsonBytes(integrationData))
                    .contentType(TestUtil.APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andExpect(content().string(HttpStatusResponseType.SUCCESSFUL_OPERATION.getReasonPhrase()));
        }
    }


    public RouteDefinition getRoute(String uri){
        List<RouteDefinition> list = springCamelContext.getRouteDefinitions();
        return list.stream().filter(r->r.getInputs().stream().anyMatch(i->i.getUri().equalsIgnoreCase(uri))).findFirst().get();
    }

    public void mockRoute(String mockPrefix,String uri) throws Exception {
        RouteDefinition dslRoute = getRoute(uri);
        dslRoute.adviceWith(springCamelContext, new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // intercept sending to direct:uri and do something else
                interceptSendToEndpoint(uri)
                        //.skipSendToOriginalEndpoint()
                        //.to("log:foo")
                        .to(mockPrefix+":"+uri);
            }
        });
    }

    public void mockRoute(String mockPrefix,String originalUri, String mockUri) throws Exception {
        RouteDefinition dslRoute = getRoute(originalUri);
        dslRoute.adviceWith(springCamelContext, new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // intercept sending to direct:uri and do something else
                interceptSendToEndpoint(originalUri)
                        //.skipSendToOriginalEndpoint()
                        //.to("log:foo")
                        .to(mockUri);
            }
        });
    }

    public void mockRouteSkipSendToOriginalEndpoint(String mockPrefix,String uri) throws Exception {
        RouteDefinition dslRoute = getRoute(uri);
        dslRoute.adviceWith(springCamelContext, new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // intercept sending to direct:uri and do something else
                interceptSendToEndpoint(uri)
                        .skipSendToOriginalEndpoint()
                        //.to("log:foo")
                        .to(mockPrefix+":"+uri);
            }
        });
    }

    public void mockRouteSkipSendToOriginalEndpoint(String mockPrefix,String originalUri, String mockUri) throws Exception {
        RouteDefinition dslRoute = getRoute(originalUri);
        dslRoute.adviceWith(springCamelContext, new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // intercept sending to direct:uri and do something else
                interceptSendToEndpoint(originalUri)
                        .skipSendToOriginalEndpoint()
                        //.to("log:foo")
                        .to(mockUri);
            }
        });
    }

    public SpringCamelContext getSpringCamelContext() {
        return springCamelContext;
    }

    public void setSpringCamelContext(SpringCamelContext springCamelContext) {
        this.springCamelContext = springCamelContext;
    }
}
