package com.intrasoft.csp.integration.sandbox.server.internal;

import com.intrasoft.csp.commons.model.*;
import com.intrasoft.csp.commons.routes.CamelRoutes;
import com.intrasoft.csp.integration.MockUtils;
import com.intrasoft.csp.server.CspApp;
import com.intrasoft.csp.server.routes.RouteUtils;
import com.intrasoft.csp.server.service.CamelRestService;
import org.apache.camel.*;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.contains;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {CspApp.class, MockUtils.class},
        properties = {
                "csp.retry.backOffPeriod:10",
                "csp.retry.maxAttempts:1",
                "embedded.activemq.start:false",
                "apache.camel.use.activemq:false",
        })
@MockEndpointsAndSkip("http:*")
public class CspServerInternalSandboxTestFlow1dataTypes implements CamelRoutes {
    private static final Logger LOG = LoggerFactory.getLogger(CspServerInternalSandboxTest.class);

    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX + ":" + DIRECT + ":" + DSL)
    private MockEndpoint mockedDsl;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX + ":" + DIRECT + ":" + DDL)
    private MockEndpoint mockedDdl;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX + ":" + DIRECT + ":" + TC)
    private MockEndpoint mockedTC;

    //deprecated
    /*@EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+TCT)
    private MockEndpoint mockedTCT;*/

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX + ":" + DIRECT + ":" + ECSP)
    private MockEndpoint mockedEcsp;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+ELASTIC)
    private MockEndpoint mockedElastic;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+APP)
    private MockEndpoint mockedApp;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+DCL)
    private MockEndpoint mockedDcl;

    @MockBean
    CamelRestService camelRestService;

    @Autowired
    MockUtils mockUtils;

    @Autowired
    RouteUtils routes;

    @Autowired
    SpringCamelContext springCamelContext;

    @Autowired
    Environment env;

    private Integer numOfCspsToTest = 3;
    private Integer currentCspId = 0;
    private HashMap<IntegrationDataType, Integer> internalApps = new HashMap<>();

    String elasticUri;

    @Before
    public void init() throws Exception {
        mvc = webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.initMocks(this);
        mockUtils.setSpringCamelContext(springCamelContext);


        String elasticProtocol = env.getProperty("elastic.protocol");
        String elasticHost= env.getProperty("elastic.host");
        String elasticPort= env.getProperty("elastic.port");
        String elasticPath= env.getProperty("elastic.path");

        elasticUri = elasticProtocol + "://" + elasticHost + ":" + elasticPort + elasticPath;


        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.apply(DSL), mockedDsl.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.apply(DDL), mockedDdl.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.apply(ECSP), mockedEcsp.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.apply(TC), mockedTC.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.apply(ELASTIC), mockedElastic.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.apply(APP), mockedApp.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.apply(DCL), mockedDcl.getEndpointUri());

        //Initialize internalApps Hashmap according applicatiopn.properties (internal section)
        internalApps.put(IntegrationDataType.THREAT, 1);
        internalApps.put(IntegrationDataType.ARTEFACT, 2);
        internalApps.put(IntegrationDataType.TRUSTCIRCLE, 1);

        Mockito.when(camelRestService.send(anyString(), anyObject(), eq("GET"), eq(Team.class)))
                .thenReturn(mockUtils.getMockedTeam(1, "http://external.csp%s.com"))
                .thenReturn(mockUtils.getMockedTeam(2, "http://external.csp%s.com"))
                .thenReturn(mockUtils.getMockedTeam(3, "http://external.csp%s.com"));

        Mockito.when(camelRestService.send(contains(elasticUri),anyObject(),anyString()))
                .thenReturn(mockUtils.getMockedElasticSearchResponse(2));

    }

    @DirtiesContext
    @Test
    public void testDslFlow1PostDataTypeThreat() throws Exception {
        Mockito.when(camelRestService.sendAndGetList(anyString(), anyObject(), eq("GET"), eq(TrustCircle.class), anyObject()))
                .thenReturn(mockUtils.getAllMockedTrustCircles(this.numOfCspsToTest, IntegrationDataType.tcNamingConventionForShortName.get(IntegrationDataType.THREAT)));

        Mockito.when(camelRestService.send(anyString(), anyObject(), eq("GET"), eq(TrustCircle.class)))
                .thenReturn(mockUtils.getMockedTrustCircle(this.numOfCspsToTest, IntegrationDataType.tcNamingConventionForShortName.get(IntegrationDataType.THREAT)));

        mockUtils.sendFlow1Data(mvc, false, true, IntegrationDataType.THREAT, "POST");

        _flowImpl(IntegrationDataType.THREAT);

        //Thread.sleep(10*1000); //to avoid "Rejecting received message because of the listener container having been stopped in the meantime"
        //be careful when debugging, you might miss breakpoints if the time is not enough
    }

    @DirtiesContext
    @Test
    public void testDslFlow1PutDataTypeThreat() throws Exception {
        Mockito.when(camelRestService.sendAndGetList(anyString(), anyObject(), eq("GET"), eq(TrustCircle.class), anyObject()))
                .thenReturn(mockUtils.getAllMockedTrustCircles(this.numOfCspsToTest, IntegrationDataType.tcNamingConventionForShortName.get(IntegrationDataType.THREAT)));

        Mockito.when(camelRestService.send(anyString(), anyObject(), eq("GET"), eq(TrustCircle.class)))
                .thenReturn(mockUtils.getMockedTrustCircle(this.numOfCspsToTest, IntegrationDataType.tcNamingConventionForShortName.get(IntegrationDataType.THREAT)));

        mockUtils.sendFlow1Data(mvc, false, true, IntegrationDataType.THREAT, "PUT");

        _flowImpl(IntegrationDataType.THREAT);

        //Thread.sleep(10*1000); //to avoid "Rejecting received message because of the listener container having been stopped in the meantime"
        //be careful when debugging, you might miss breakpoints if the time is not enough
    }

    @DirtiesContext
    @Test
    public void testDslFlow1PostDataTypeArtefact() throws Exception {
        Mockito.when(camelRestService.sendAndGetList(anyString(), anyObject(), eq("GET"), eq(TrustCircle.class), anyObject()))
                .thenReturn(mockUtils.getAllMockedTrustCircles(this.numOfCspsToTest, IntegrationDataType.tcNamingConventionForShortName.get(IntegrationDataType.ARTEFACT)));

        Mockito.when(camelRestService.send(anyString(), anyObject(), eq("GET"), eq(TrustCircle.class)))
                .thenReturn(mockUtils.getMockedTrustCircle(this.numOfCspsToTest, IntegrationDataType.tcNamingConventionForShortName.get(IntegrationDataType.ARTEFACT)));

        mockUtils.sendFlow1Data(mvc, false, true, IntegrationDataType.ARTEFACT, "POST");

        _flowImpl(IntegrationDataType.ARTEFACT);

        //Thread.sleep(10*1000); //to avoid "Rejecting received message because of the listener container having been stopped in the meantime"
        //be careful when debugging, you might miss breakpoints if the time is not enough
    }

    @DirtiesContext
    @Test
    public void testDslFlow1PutDataTypeArtefact() throws Exception {
        Mockito.when(camelRestService.sendAndGetList(anyString(), anyObject(), eq("GET"), eq(TrustCircle.class), anyObject()))
                .thenReturn(mockUtils.getAllMockedTrustCircles(this.numOfCspsToTest, IntegrationDataType.tcNamingConventionForShortName.get(IntegrationDataType.ARTEFACT)));

        Mockito.when(camelRestService.send(anyString(), anyObject(), eq("GET"), eq(TrustCircle.class)))
                .thenReturn(mockUtils.getMockedTrustCircle(this.numOfCspsToTest, IntegrationDataType.tcNamingConventionForShortName.get(IntegrationDataType.ARTEFACT)));

        mockUtils.sendFlow1Data(mvc, false, true, IntegrationDataType.ARTEFACT, "PUT");

        _flowImpl(IntegrationDataType.ARTEFACT);

        //Thread.sleep(10*1000); //to avoid "Rejecting received message because of the listener container having been stopped in the meantime"
        //be careful when debugging, you might miss breakpoints if the time is not enough
    }

    @DirtiesContext
    @Test
    public void testDslFlow1PostDataTypeTrustcircle() throws Exception {
        Mockito.when(camelRestService.sendAndGetList(anyString(), anyObject(), eq("GET"), eq(TrustCircle.class), anyObject()))
                .thenReturn(mockUtils.getAllMockedTrustCircles(this.numOfCspsToTest, IntegrationDataType.tcNamingConventionForShortName.get(IntegrationDataType.TRUSTCIRCLE)));

        Mockito.when(camelRestService.send(anyString(), anyObject(), eq("GET"), eq(TrustCircle.class)))
                .thenReturn(mockUtils.getMockedTrustCircle(this.numOfCspsToTest, IntegrationDataType.tcNamingConventionForShortName.get(IntegrationDataType.TRUSTCIRCLE)));

        mockUtils.sendFlow1Data(mvc, false, true, IntegrationDataType.TRUSTCIRCLE, "POST");

        _flowImpl(IntegrationDataType.TRUSTCIRCLE);

        //Thread.sleep(10*1000); //to avoid "Rejecting received message because of the listener container having been stopped in the meantime"
        //be careful when debugging, you might miss breakpoints if the time is not enough
    }

    @DirtiesContext
    @Test
    public void testDslFlow1PutDataTypeTrustcircle() throws Exception {
        Mockito.when(camelRestService.sendAndGetList(anyString(), anyObject(), eq("GET"), eq(TrustCircle.class), anyObject()))
                .thenReturn(mockUtils.getAllMockedTrustCircles(this.numOfCspsToTest, IntegrationDataType.tcNamingConventionForShortName.get(IntegrationDataType.TRUSTCIRCLE)));

        Mockito.when(camelRestService.send(anyString(), anyObject(), eq("GET"), eq(TrustCircle.class)))
                .thenReturn(mockUtils.getMockedTrustCircle(this.numOfCspsToTest, IntegrationDataType.tcNamingConventionForShortName.get(IntegrationDataType.TRUSTCIRCLE)));

        mockUtils.sendFlow1Data(mvc, false, true, IntegrationDataType.TRUSTCIRCLE, "PUT");

        _flowImpl(IntegrationDataType.TRUSTCIRCLE);

        //Thread.sleep(10*1000); //to avoid "Rejecting received message because of the listener container having been stopped in the meantime"
        //be careful when debugging, you might miss breakpoints if the time is not enough
    }



    private void _flowImpl(IntegrationDataType dataType) throws Exception {
       /*
        DSL
         */
        //Expect 1-message
        mockedDsl.expectedMessageCount(1);
        mockedDsl.assertIsSatisfied();


        List<Exchange> list = mockedDsl.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(dataType));
        }

        /*
        APP
         */
        //Expect message count according to application.properties
        mockedApp.expectedMessageCount(internalApps.get(dataType));
        mockedApp.assertIsSatisfied();

        list = mockedApp.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(dataType));
        }

        //DDL
        mockedDdl.expectedMessageCount(1);
        mockedDdl.assertIsSatisfied();

        list = mockedDdl.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(dataType));
        }

        //DCL
        mockedDcl.expectedMessageCount(1);
        mockedDcl.assertIsSatisfied();

        list = mockedDcl.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(dataType));
        }

        //TC
        mockedTC.expectedMessageCount(1);
        mockedTC.assertIsSatisfied();

        list = mockedTC.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(dataType));
        }

        //ESCP
        mockedEcsp.expectedMessageCount(3);
        mockedEcsp.assertIsSatisfied();

        list = mockedEcsp.getReceivedExchanges();
        int i=0;
        for (Exchange exchange : list) {
            i++;
            Message in = exchange.getIn();
            EnhancedTeamDTO enhancedTeamDTO = in.getBody(EnhancedTeamDTO.class);
            assertThat(enhancedTeamDTO.getTeam().getUrl(), is("http://external.csp"+i+".com"));
        }

        //ELASTIC
        if(IntegrationDataType.TRUSTCIRCLE.equals(dataType)){
            mockedElastic.expectedMessageCount(0);
        }else {
            mockedElastic.expectedMessageCount(1);
        }
        mockedElastic.assertIsSatisfied();

        list = mockedElastic.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(dataType));
        }
    }

}