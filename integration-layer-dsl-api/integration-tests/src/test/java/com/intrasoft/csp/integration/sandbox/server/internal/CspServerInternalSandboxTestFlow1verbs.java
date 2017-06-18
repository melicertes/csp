package com.intrasoft.csp.integration.sandbox.server.internal;

import com.intrasoft.csp.commons.model.*;
import com.intrasoft.csp.commons.routes.CamelRoutes;
import com.intrasoft.csp.integration.MockUtils;
import com.intrasoft.csp.server.CspApp;
import com.intrasoft.csp.server.routes.RouteUtils;
import com.intrasoft.csp.server.service.CamelRestService;
import org.apache.camel.*;
import org.apache.camel.component.http.HttpMethods;
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

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {CspApp.class, MockUtils.class},
        properties = {
                "csp.retry.backOffPeriod:10",
                "csp.retry.maxAttempts:1",
                "embedded.activemq.start:false",
                "apache.camel.use.activemq:false",
                "internal.vulnerability.apps:taranis, misp"
        })
@MockEndpointsAndSkip("http:*")
public class CspServerInternalSandboxTestFlow1verbs implements CamelRoutes {
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

    String elasticUri;

    private Integer numOfCspsToTest = 3;
    private Integer currentCspId = 0;
    private final IntegrationDataType dataTypeToTest = IntegrationDataType.VULNERABILITY;
    private final String applicationId = "taranis";

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

        Mockito.when(camelRestService.sendAndGetList(anyString(), anyObject(), eq("GET"), eq(TrustCircle.class), anyObject()))
                .thenReturn(mockUtils.getAllMockedTrustCircles(this.numOfCspsToTest, IntegrationDataType.tcNamingConventionForShortName.get(this.dataTypeToTest)));

        Mockito.when(camelRestService.send(anyString(), anyObject(), eq("GET"), eq(TrustCircle.class)))
                .thenReturn(mockUtils.getMockedTrustCircle(this.numOfCspsToTest, IntegrationDataType.tcNamingConventionForShortName.get(this.dataTypeToTest)));

        Mockito.when(camelRestService.send(anyString(), anyObject(), eq("GET"), eq(Team.class)))
                .thenReturn(mockUtils.getMockedTeam(1, "http://external.csp%s.com"))
                .thenReturn(mockUtils.getMockedTeam(2, "http://external.csp%s.com"))
                .thenReturn(mockUtils.getMockedTeam(3, "http://external.csp%s.com"));

        Mockito.when(camelRestService.send(Matchers.contains(elasticUri),anyObject(),anyString()))
                .thenReturn(mockUtils.getMockedElasticSearchResponse(2));
    }

    @DirtiesContext
    @Test
    public void testDslFlow1PostToShare() throws Exception {
        mockUtils.sendFlow1Data(mvc, applicationId,false, true, this.dataTypeToTest, HttpMethods.POST.name());

        _toSharePostPutFlowImpl();

        //Thread.sleep(10*1000); //to avoid "Rejecting received message because of the listener container having been stopped in the meantime"
        //be careful when debugging, you might miss breakpoints if the time is not enough
    }

    @DirtiesContext
    @Test
    public void testDslFlow1PutToShare() throws Exception {
        mockUtils.sendFlow1Data(mvc, applicationId,false, true, this.dataTypeToTest, HttpMethods.PUT.name());

        _toSharePostPutFlowImpl();

        //Thread.sleep(10*1000); //to avoid "Rejecting received message because of the listener container having been stopped in the meantime"
        //be careful when debugging, you might miss breakpoints if the time is not enough
    }

    @DirtiesContext
    @Test
    public void testDslFlow1DeleteToShare() throws Exception {
        mockUtils.sendFlow1Data(mvc, applicationId,false, true, this.dataTypeToTest, HttpMethods.DELETE.name());

        _deleteFlowImpl();

        //Thread.sleep(10*1000); //to avoid "Rejecting received message because of the listener container having been stopped in the meantime"
        //be careful when debugging, you might miss breakpoints if the time is not enough
    }




    @DirtiesContext
    @Test
    public void testDslFlow1PostNotToShare() throws Exception {
        mockUtils.sendFlow1Data(mvc, applicationId,false, false, this.dataTypeToTest, HttpMethods.POST.name());

        _notToSharePostPutFlowImpl();

        //Thread.sleep(10*1000); //to avoid "Rejecting received message because of the listener container having been stopped in the meantime"
        //be careful when debugging, you might miss breakpoints if the time is not enough
    }

    @DirtiesContext
    @Test
    public void testDslFlow1PutNotToShare() throws Exception {
        mockUtils.sendFlow1Data(mvc, applicationId,false, false, this.dataTypeToTest, HttpMethods.PUT.name());

        _notToSharePostPutFlowImpl();

        //Thread.sleep(10*1000); //to avoid "Rejecting received message because of the listener container having been stopped in the meantime"
        //be careful when debugging, you might miss breakpoints if the time is not enough
    }

    @DirtiesContext
    @Test
    public void testDslFlow1DeleteNotToShare() throws Exception {
        mockUtils.sendFlow1Data(mvc, applicationId,false, false, this.dataTypeToTest, HttpMethods.DELETE.name());

        _deleteFlowImpl();

        //Thread.sleep(10*1000); //to avoid "Rejecting received message because of the listener container having been stopped in the meantime"
        //be careful when debugging, you might miss breakpoints if the time is not enough
    }


    private void _toSharePostPutFlowImpl() throws Exception {
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
            assertThat(data.getDataType(), is(this.dataTypeToTest));
        }

        /*
        APP
         */
        // The data type to test is defined in line 101-> private IntegrationDataType dataTypeToTest = IntegrationDataType.VULNERABILITY;
        // The application id is "taranis"
        // Expect 1-messages according to application.properties (internal.vulnerability.apps:taranis, misp)
        // *since taranis is emitting the message, it should be excluded from receiving his own message
        mockedApp.expectedMessageCount(1);
        mockedApp.assertIsSatisfied();

        list = mockedApp.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(this.dataTypeToTest));
        }

        //DDL
        mockedDdl.expectedMessageCount(1);
        mockedDdl.assertIsSatisfied();

        list = mockedDdl.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(this.dataTypeToTest));
        }

        //DCL
        mockedDcl.expectedMessageCount(1);
        mockedDcl.assertIsSatisfied();

        list = mockedDcl.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(this.dataTypeToTest));
        }

        //TC
        mockedTC.expectedMessageCount(1);
        mockedTC.assertIsSatisfied();

        list = mockedTC.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(this.dataTypeToTest));
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
        mockedElastic.expectedMessageCount(1);
        mockedElastic.assertIsSatisfied();

        list = mockedElastic.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(this.dataTypeToTest));
        }
    }

    private void _notToSharePostPutFlowImpl() throws Exception {
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
            assertThat(data.getDataType(), is(this.dataTypeToTest));
        }

        /*
        APP
         */
        // The data type to test is defined in line 101-> private IntegrationDataType dataTypeToTest = IntegrationDataType.VULNERABILITY;
        // The application id is "taranis"
        // Expect 1-messages according to application.properties (internal.vulnerability.apps:taranis, misp)
        // *since taranis is emitting the message, it should be excluded from receiving his own message
        mockedApp.expectedMessageCount(1);
        mockedApp.assertIsSatisfied();

        list = mockedApp.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(this.dataTypeToTest));
        }

        //DDL
        mockedDdl.expectedMessageCount(1);
        mockedDdl.assertIsSatisfied();

        list = mockedDdl.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(this.dataTypeToTest));
        }

        /*
        DCL
         */
        //Do not expect something for NotToShare
        mockedDcl.expectedMessageCount(0);
        mockedDcl.assertIsSatisfied();


        //TC and ESCP are not called from DCL


        //ELASTIC
        mockedElastic.expectedMessageCount(1);
        mockedElastic.assertIsSatisfied();

        list = mockedElastic.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(this.dataTypeToTest));
        }
    }

    private void _deleteFlowImpl() throws Exception {
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
            assertThat(data.getDataType(), is(this.dataTypeToTest));
        }

        /*
        APP
         */
        // The data type to test is defined in line 101-> private IntegrationDataType dataTypeToTest = IntegrationDataType.VULNERABILITY;
        // The application id is "taranis"
        // Expect 1-messages according to application.properties (internal.vulnerability.apps:taranis, misp)
        // *since taranis is emitting the message, it should be excluded from receiving his own message
        mockedApp.expectedMessageCount(1);
        mockedApp.assertIsSatisfied();

        list = mockedApp.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(this.dataTypeToTest));
        }

        //DDL
        mockedDdl.expectedMessageCount(1);
        mockedDdl.assertIsSatisfied();

        list = mockedDdl.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(this.dataTypeToTest));
        }

        /*
        DCL
         */
        //DELETE verb is not propagated to DCL
        mockedDcl.expectedMessageCount(0);
        mockedDcl.assertIsSatisfied();

        //ELASTIC
        mockedElastic.expectedMessageCount(1);
        mockedElastic.assertIsSatisfied();

        list = mockedElastic.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(this.dataTypeToTest));
        }
    }
}