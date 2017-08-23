package com.intrasoft.csp.server.policy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.anon.client.AnonClient;
import com.intrasoft.csp.anon.commons.model.IntegrationAnonData;
import com.intrasoft.csp.commons.model.*;
import com.intrasoft.csp.commons.routes.CamelRoutes;
import com.intrasoft.csp.server.CspApp;
import com.intrasoft.csp.server.policy.domain.model.SharingPolicyAction;
import com.intrasoft.csp.server.routes.RouteUtils;
import com.intrasoft.csp.server.service.CamelRestService;
import com.intrasoft.csp.server.utils.MockUtils;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {CspApp.class,MockUtils.class},
        properties = {
                "csp.retry.backOffPeriod:10",
                "csp.retry.maxAttempts:1",
                "embedded.activemq.start:false",
                "apache.camel.use.activemq:false",
        })
@MockEndpointsAndSkip("http:*")
public class SharingPolicyServiceTest implements CamelRoutes {
    private static final Logger LOG = LoggerFactory.getLogger(SharingPolicyServiceTest.class);

    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+DSL)
    private MockEndpoint mockedDsl;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+DDL)
    private MockEndpoint mockedDdl;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+TC)
    private MockEndpoint mockedTC;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+ECSP)
    private MockEndpoint mockedEcsp;

    @MockBean
    CamelRestService camelRestService;

    @MockBean
    SharingPolicyService sharingPolicyService;

    @MockBean
    AnonClient anonClient;

    @Autowired
    MockUtils mockUtils;

    @Autowired
    RouteUtils routes;

    @Autowired
    SpringCamelContext springCamelContext;

    @Autowired
    ObjectMapper objectMapper;

    DataParams anonObject;
    @Before
    public void init() throws Exception {
        mvc = webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.initMocks(this);
        mockUtils.setSpringCamelContext(springCamelContext);
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX,routes.apply(DSL),mockedDsl.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX,routes.apply(DDL), mockedDdl.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX,routes.apply(ECSP), mockedEcsp.getEndpointUri());
        Mockito.when(camelRestService.sendAndGetList(anyString(), anyObject(), eq("GET"), eq(TrustCircle.class),anyObject()))
                .thenReturn(mockUtils.getAllMockedTrustCircles(3));
        Mockito.when(camelRestService.send(anyString(), anyObject(), eq("GET"), eq(TrustCircle.class)))
                .thenReturn(mockUtils.getMockedTrustCircle(3));
        Mockito.when(camelRestService.send(anyString(), anyObject(), eq("GET"), eq(Team.class)))
                .thenReturn(mockUtils.getMockedTeam(1,"http://external.csp%s.com"))
                .thenReturn(mockUtils.getMockedTeam(2,"http://external.csp%s.com"))
                .thenReturn(mockUtils.getMockedTeam(3,"http://external.csp%s.com"));
        IntegrationAnonData integrationAnonData = new IntegrationAnonData();

        anonObject = new DataParams();
        anonObject.setCspId("AnonymizedCspId");
        anonObject.setApplicationId("AnonymizedApplicationId");

        integrationAnonData.setDataObject(anonObject);
        Mockito.when(anonClient.postAnonData(anyObject())).thenReturn(integrationAnonData);
    }

    @Test
    public void evaluateTest(){
        SharingPolicyAction action = sharingPolicyService.evaluate(IntegrationDataType.ARTEFACT);
        assertThat(action, is(SharingPolicyAction.NO_ACTION_FOUND));
    }

    @DirtiesContext
    @Test
    public void dslFlow1TestUsingCamelEndpoint() throws Exception {
        Mockito.when(sharingPolicyService.evaluate(eq(IntegrationDataType.INCIDENT))).thenReturn(SharingPolicyAction.SHARE_ANONYMIZED);
        mockUtils.sendFlow1IntegrationData(mvc,false);

        mockedEcsp.expectedMessageCount(3);
        mockedEcsp.assertIsSatisfied();
        List<Exchange> list = mockedEcsp.getReceivedExchanges();
        int i=0;
        for (Exchange exchange : list) {
            i++;
            Message in = exchange.getIn();
            EnhancedTeamDTO enhancedTeamDTO = in.getBody(EnhancedTeamDTO.class);
            assertThat(enhancedTeamDTO.getTeam().getUrl(), is("http://external.csp"+i+".com"));
            String anonObjectJsonStrExpected = objectMapper.writeValueAsString(anonObject);
            String anonObjectJsonStrToCompare = objectMapper.writeValueAsString(enhancedTeamDTO.getIntegrationData().getDataObject());
            assertThat(anonObjectJsonStrToCompare,is(anonObjectJsonStrExpected));
        }
    }
}
