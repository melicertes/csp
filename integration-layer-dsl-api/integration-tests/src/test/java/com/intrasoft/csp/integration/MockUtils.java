package com.intrasoft.csp.integration;

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

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataType(IntegrationDataType.INCIDENT);

        DataParams dataParams = new DataParams();
        dataParams.setApplicationId("test1");
        dataParams.setCspId("testCspId");
        dataParams.setRecordId("recordId");
        dataParams.setDateTime(DateTime.now());
        SharingParams sharingParams = new SharingParams();
        sharingParams.setIsExternal(isExternal);
        sharingParams.setToShare(true);
        integrationData.setDataParams(dataParams);
        integrationData.setDataObject(dataParams);
        integrationData.setSharingParams(sharingParams);
        mvc.perform(post("/v"+REST_API_V1+"/"+DSL_INTEGRATION_DATA).accept(MediaType.TEXT_PLAIN)
                .content(TestUtil.convertObjectToJsonBytes(integrationData))
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().string(HttpStatusResponseType.SUCCESSFUL_OPERATION.getReasonPhrase()));
    }




    public void sendFlow1Data(MockMvc mvc, Boolean isExternal, Boolean toShare, IntegrationDataType dataType, String httpMethod) throws Exception {
        sendFlow1Data(mvc,"taranis", isExternal,toShare,dataType,httpMethod);
    }

    public void sendFlow1Data(MockMvc mvc, String applicationId, Boolean isExternal, Boolean toShare, IntegrationDataType dataType, String httpMethod) throws Exception {
        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataType(dataType);
        SharingParams sharingParams = new SharingParams();
        sharingParams.setIsExternal(isExternal);
        sharingParams.setToShare(toShare);
        integrationData.setSharingParams(sharingParams);
        DataParams dataParams = new DataParams();
        dataParams.setRecordId("222");
        dataParams.setDateTime(DateTime.now());
        dataParams.setApplicationId(applicationId);
        dataParams.setCspId("CERT-GR");
        integrationData.setDataParams(dataParams);
        //integrationData.setDataObject("{\"t\":\"1234\"}");
        integrationData.setDataObject(dataParams);


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


    public void sendFlow2Data(MockMvc mvc, String applicationId, Boolean isExternal, Boolean toShare, String cspId, IntegrationDataType dataType, String httpMethod) throws Exception {
        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataType(dataType);
        SharingParams sharingParams = new SharingParams();
        sharingParams.setIsExternal(isExternal);
        sharingParams.setToShare(toShare);
        integrationData.setSharingParams(sharingParams);
        DataParams dataParams = new DataParams();
        dataParams.setRecordId("222");
        dataParams.setApplicationId(applicationId);
        dataParams.setDateTime(DateTime.now());
        dataParams.setCspId(cspId);
        integrationData.setDataParams(dataParams);
        integrationData.setDataObject("{\"t\":\"1234\"}");


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
