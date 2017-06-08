package com.intrasoft.csp.integration.sandbox.server.internal;

import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;

import com.intrasoft.csp.commons.model.Team;
import com.intrasoft.csp.commons.model.TrustCircle;
import com.intrasoft.csp.commons.routes.CamelRoutes;
import com.intrasoft.csp.integration.MockUtils;
import com.intrasoft.csp.server.CspApp;
import com.intrasoft.csp.server.routes.RouteUtils;
import com.intrasoft.csp.server.service.CamelRestService;
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
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
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
public class CspServerInternalSandboxTestFlow2 {

    private static final Logger LOG = LoggerFactory.getLogger(CspServerInternalSandboxTest.class);

    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX + ":" + CamelRoutes.DIRECT + ":" + CamelRoutes.DSL)
    private MockEndpoint mockedDsl;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX + ":" + CamelRoutes.DIRECT + ":" + CamelRoutes.TC)
    private MockEndpoint mockedTC;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX + ":" + CamelRoutes.DIRECT + ":" + CamelRoutes.APP)
    private MockEndpoint mockedApp;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX + ":" + CamelRoutes.DIRECT + ":" + CamelRoutes.EDCL)
    private MockEndpoint mockedEDcl;

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
    private final IntegrationDataType dataTypeToTest = IntegrationDataType.VULNERABILITY;
    private final String applicationId = "taranis";


    @Before
    public void init() throws Exception {
        mvc = webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.initMocks(this);
        mockUtils.setSpringCamelContext(springCamelContext);

        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.apply(CamelRoutes.DSL), mockedDsl.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.apply(CamelRoutes.TC), mockedTC.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.apply(CamelRoutes.APP), mockedApp.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.apply(CamelRoutes.EDCL), mockedEDcl.getEndpointUri());

        Mockito.when(camelRestService.sendAndGetList(anyString(), anyObject(), eq("GET"), eq(TrustCircle.class), anyObject()))
                .thenReturn(mockUtils.getAllMockedTrustCircles(this.numOfCspsToTest, this.dataTypeToTest.name()));

        Mockito.when(camelRestService.send(anyString(), anyObject(), eq("GET"), eq(TrustCircle.class)))
                .thenReturn(mockUtils.getMockedTrustCircle(this.numOfCspsToTest, this.dataTypeToTest.name()));

        Mockito.when(camelRestService.send(anyString(), anyObject(), eq("GET"), eq(Team.class)))
                .thenReturn(mockUtils.getMockedTeam(1, "http://external.csp%s.com", "CERT-GR"))
                .thenReturn(mockUtils.getMockedTeam(2, "http://external.csp%s.com", "CERT-DE"))
                .thenReturn(mockUtils.getMockedTeam(3, "http://external.csp%s.com", "CERT-FR"));
    }

    @DirtiesContext
    @Test
    public void testDslFlow2PostAuthorized() throws Exception {
        /*
        isExternal will be false in Flow 2, and TcProcessor will set it to true
        toShare will be true in Flow 2
         */
        //For authorized flow, set cspId to have a value from the initialized in Mockito (CERT-GR, CERT-DE or CERT-FR)
        mockUtils.sendFlow2Data(mvc, applicationId, false, true, "CERT-GR", this.dataTypeToTest, "POST");

        _authorizedFlowImpl();

        //Thread.sleep(10*1000); //to avoid "Rejecting received message because of the listener container having been stopped in the meantime"
        //be careful when debugging, you might miss breakpoints if the time is not enough
    }

    @DirtiesContext
    @Test
    public void testDslFlow2PutAuthorized() throws Exception {
        /*
        isExternal will be false in Flow 2, and TcProcessor will set it to true
        toShare will be true in Flow 2
         */
        //For authorized flow, set cspId to have a value from the initialized in Mockito (CERT-GR, CERT-DE or CERT-FR)
        mockUtils.sendFlow2Data(mvc, applicationId, false, true, "CERT-GR", this.dataTypeToTest, "PUT");

        _authorizedFlowImpl();

        //Thread.sleep(10*1000); //to avoid "Rejecting received message because of the listener container having been stopped in the meantime"
        //be careful when debugging, you might miss breakpoints if the time is not enough
    }

    @DirtiesContext
    @Test
    public void testDslFlow2PostNotAuthorized() throws Exception {
        /*
        isExternal will be false in Flow 2, and TcProcessor will set it to true
        toShare will be true in Flow 2
         */
        //For NOT authorized flow, set cspId to have a value different than the ones initialized in Mockito (CERT-GR, CERT-DE or CERT-FR)
        mockUtils.sendFlow2Data(mvc, applicationId, false, true, "CERT-DUMMY-GR", this.dataTypeToTest, "POST");

        _notAuthorizedFlowImpl();

        //Thread.sleep(10*1000); //to avoid "Rejecting received message because of the listener container having been stopped in the meantime"
        //be careful when debugging, you might miss breakpoints if the time is not enough
    }

    @DirtiesContext
    @Test
    public void testDslFlow2PutNotAuthorized() throws Exception {
        /*
        isExternal will be false in Flow 2, and TcProcessor will set it to true
        toShare will be true in Flow 2
         */
        //For NOT authorized flow, set cspId to have a value different than the ones initialized in Mockito (CERT-GR, CERT-DE or CERT-FR)
        mockUtils.sendFlow2Data(mvc, applicationId, false, true, "CERT-DUMMY-GR", this.dataTypeToTest, "PUT");

        _notAuthorizedFlowImpl();

        //Thread.sleep(10*1000); //to avoid "Rejecting received message because of the listener container having been stopped in the meantime"
        //be careful when debugging, you might miss breakpoints if the time is not enough
    }

    private void _authorizedFlowImpl() throws Exception {
                /*
        External DCL: expect 1-message
         */
        mockedEDcl.expectedMessageCount(1);
        mockedEDcl.assertIsSatisfied();

        //assert datatype, isExternal (?), toShare
        List<Exchange> list = mockedEDcl.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(this.dataTypeToTest));
            //assertThat(data.getSharingParams().getIsExternal(), is(false));
            assertThat(data.getSharingParams().getToShare(), is(true));
        }


        /*
        TC: expect 1-message for authorized calls
         */
        mockedTC.expectedMessageCount(1);
        mockedTC.assertIsSatisfied();

        //assert datatype, isExternal (is changed for authorized), toShare
        list = mockedTC.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(this.dataTypeToTest));
            assertThat(data.getSharingParams().getIsExternal(), is(true));
            assertThat(data.getSharingParams().getToShare(), is(true));
        }

       /*
        DSL: expect 1-message
         */
        mockedDsl.expectedMessageCount(1);
        mockedDsl.assertIsSatisfied();


        list = mockedDsl.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(this.dataTypeToTest));
            assertThat(data.getSharingParams().getIsExternal(), is(true));
            assertThat(data.getSharingParams().getToShare(), is(true));
        }

        /*
        APP
         */
        // The data type to test is defined in class -> private IntegrationDataType dataTypeToTest = IntegrationDataType.VULNERABILITY;
        // The application id is "taranis"
        // Expect 1-messages according to application.properties (external.vulnerability.apps:taranis)
        mockedApp.expectedMessageCount(1);
        mockedApp.assertIsSatisfied();

        list = mockedApp.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(this.dataTypeToTest));
            assertThat(data.getSharingParams().getIsExternal(), is(true));
            assertThat(data.getSharingParams().getToShare(), is(true));
        }
    }

    private void _notAuthorizedFlowImpl() throws Exception {
        /*
        External DCL: expect 1-message
         */
        mockedEDcl.expectedMessageCount(1);
        mockedEDcl.assertIsSatisfied();

        //assert datatype, isExternal (?), toShare
        List<Exchange> list = mockedEDcl.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(this.dataTypeToTest));
            //assertThat(data.getSharingParams().getIsExternal(), is(false));
            assertThat(data.getSharingParams().getToShare(), is(true));
        }


        /*
        TC: expect 1-message for authorized calls
         */
        mockedTC.expectedMessageCount(1);
        mockedTC.assertIsSatisfied();

        //assert datatype, isExternal (not changed for NOT authorized), toShare
        list = mockedTC.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(this.dataTypeToTest));
            assertThat(data.getSharingParams().getIsExternal(), is(false));
            assertThat(data.getSharingParams().getToShare(), is(true));
        }


       /*
        DSL: expect no message, flow has ended
         */
        mockedDsl.expectedMessageCount(0);
        mockedDsl.assertIsSatisfied();


        /*
        APP: expect no message, flow has ended
         */
        mockedApp.expectedMessageCount(0);
        mockedApp.assertIsSatisfied();
    }
}
