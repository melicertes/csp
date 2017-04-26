package com.sastix.csp.integration;

import com.sastix.csp.commons.apiHttpStatusResponse.HttpStatusResponseType;
import com.sastix.csp.commons.model.*;
import com.sastix.csp.commons.routes.ContextUrl;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spring.SpringCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by iskitsas on 4/10/17.
 */
@Component
public class MockUtils {
    private static final Logger LOG = LoggerFactory.getLogger(MockUtils.class);

    SpringCamelContext springCamelContext;

    /**
     * examples: getMockedTrustCircle(3, "http://external.csp%s.com")
     * */
    public TrustCircle getMockedTrustCircle(int count){
        TrustCircle trustCircle = new TrustCircle();
        //List<String> listCsps = new ArrayList<>();
        List<Integer> teamList = new ArrayList<>();
        for(int i=0; i< count;i++) {
            //listCsps.add(String.format(strWithCountArg,""+(i+1)));
            teamList.add(i);
        }
        trustCircle.setTeams(teamList);
        return trustCircle;
    }


    public List<Team> getMockedTeams(int count, String strWithCountArg){
        List<Team> ret = new ArrayList<>();
        for(int i=0; i< count;i++) {
            Team t= new Team();
            t.setUrl(String.format(strWithCountArg,""+(i+1)));
            ret.add(t);
        }
        return ret;
    }

    public Team getMockedTeam(int id, String strWithCountArg) {
        Team team = new Team();
        team.setUrl(String.format(strWithCountArg, "" + id));


        return team;
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
        SharingParams sharingParams = new SharingParams();
        sharingParams.setIsExternal(isExternal);
        sharingParams.setToShare(true);
        integrationData.setSharingParams(sharingParams);
        mvc.perform(post(ContextUrl.DSL_INTEGRATION_DATA).accept(MediaType.TEXT_PLAIN)
                .content(TestUtil.convertObjectToJsonBytes(integrationData))
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().string(HttpStatusResponseType.SUCCESSFUL_OPERATION.getReasonPhrase()));
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

    public SpringCamelContext getSpringCamelContext() {
        return springCamelContext;
    }

    public void setSpringCamelContext(SpringCamelContext springCamelContext) {
        this.springCamelContext = springCamelContext;
    }
}
