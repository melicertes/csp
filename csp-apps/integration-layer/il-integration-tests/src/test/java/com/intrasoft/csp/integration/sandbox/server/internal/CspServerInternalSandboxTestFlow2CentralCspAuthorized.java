package com.intrasoft.csp.integration.sandbox.server.internal;

import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.Team;
import com.intrasoft.csp.commons.model.TrustCircle;
import com.intrasoft.csp.commons.routes.CamelRoutes;
import com.intrasoft.csp.server.CspApp;
import com.intrasoft.csp.server.processors.TcProcessor;
import com.intrasoft.csp.server.routes.RouteUtils;
import com.intrasoft.csp.server.service.CamelRestService;
import com.intrasoft.csp.server.utils.MockUtils;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.http.HttpMethods;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.junit.Before;
import org.junit.Rule;
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
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {CspApp.class, MockUtils.class},
        properties = {
                "spring.datasource.url:jdbc:h2:mem:csp_policy",
                "flyway.enabled:false",
                "server.name:CERT-EU",
                "csp.retry.backOffPeriod:10",
                "csp.retry.maxAttempts:1",
                "embedded.activemq.start:false",
                "apache.camel.use.activemq:false",
                "server.camel.rest.service.is.async:false" //make it sync for better handling in tests (gracefull shutdown etc.)
        })
@MockEndpointsAndSkip("http:*")
public class CspServerInternalSandboxTestFlow2CentralCspAuthorized {
    private static final Logger LOG = LoggerFactory.getLogger(CspServerInternalSandboxTestFlow2CentralCspAuthorized.class);

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
    TcProcessor tcProcessor;

    @Rule
    public OutputCapture outputCapture = new OutputCapture();

    @Autowired
    Environment env;

    private Integer numOfCspsToTest = 3;
    String tcShortNameToTest = IntegrationDataType.CTC_CSP_SHARING;//default
    private Integer currentCspId = 0;
    private final String applicationId = "taranis";
    private String cspId = "CERT-GR";
    private String tcId = "tcId";
    private String teamId = "teamId";

    @Before
    public void init() throws Exception {
        this.outputCapture.flush();
        String cspIdArg = env.getProperty("extCspId");
        if(!StringUtils.isEmpty(cspIdArg)){
            cspId = cspIdArg;
        }

        String tcIdArg = env.getProperty("extTcId");
        if(!StringUtils.isEmpty(tcIdArg)){
            tcId = tcIdArg;
        }

        String dataObjectArg = env.getProperty("dataObject");
        if(!StringUtils.isEmpty(dataObjectArg)){
            mockUtils.setDataObjectToTest(dataObjectArg);
        }

        String teamIdArg = env.getProperty("extTeamId");
        if(!StringUtils.isEmpty(teamIdArg)){
            teamId = teamIdArg;
        }
        mvc = webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.initMocks(this);
        mockUtils.setSpringCamelContext(springCamelContext);

        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.wrap(CamelRoutes.DSL), mockedDsl.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.wrap(CamelRoutes.TC), mockedTC.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.wrap(CamelRoutes.APP), mockedApp.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.wrap(CamelRoutes.EDCL), mockedEDcl.getEndpointUri());

        String urlShouldContain = tcProcessor.getTcCirclesURI();
        if(tcShortNameToTest.equalsIgnoreCase(IntegrationDataType.LTC_CSP_SHARING)){
            urlShouldContain = tcProcessor.getLocalCirclesURI();
        }

        Mockito.when(camelRestService.send(Matchers.contains(urlShouldContain), anyObject(), eq("GET"), eq(TrustCircle.class), anyObject()))
                .thenReturn(mockUtils.getMockedTrustCircle(this.numOfCspsToTest, tcShortNameToTest));

        Mockito.when(camelRestService.send(Matchers.contains(urlShouldContain), anyObject(), eq("GET"), eq(TrustCircle.class)))
                .thenReturn(mockUtils.getMockedTrustCircle(this.numOfCspsToTest, tcShortNameToTest));

        Mockito.when(camelRestService.send(anyString(), anyObject(), eq("GET"), eq(Team.class)))
                .thenReturn(mockUtils.getMockedTeam(1, "http://external.csp%s.com", "demo1-csp"))
                .thenReturn(mockUtils.getMockedTeam(2, "http://external.csp%s.com", "demo2-csp"))
                .thenReturn(mockUtils.getMockedTeam(3, "http://external.csp%s.com", "central-csp"));
    }

    @DirtiesContext
    @Test
    public void dslFlow2DataTypePostAuthorizedTCNotCentralTest() throws Exception {
        //For authorized flow, set cspId to have a value from the initialized in Mockito (CERT-GR, CERT-DE or CERT-FR)
        mockUtils.sendFlow2Data(mvc, applicationId, false, true, "demo1-csp", IntegrationDataType.TRUSTCIRCLE, HttpMethods.POST.name());
        assertAuthorizedFlow(IntegrationDataType.TRUSTCIRCLE);
    }

    @DirtiesContext
    @Test
    public void dslFlow2DataTypePostAuthorizedTCCentralTest() throws Exception {
        //For authorized flow, set cspId to have a value from the initialized in Mockito (CERT-GR, CERT-DE or CERT-FR)
        mockUtils.sendFlow2Data(mvc, applicationId, false, true, "central-csp", IntegrationDataType.TRUSTCIRCLE, HttpMethods.POST.name());
        assertAuthorizedFlow(IntegrationDataType.TRUSTCIRCLE);
    }

    private void assertAuthorizedFlow(IntegrationDataType integrationDataType) throws Exception {


        String cspIdFound = null;
        mockedEDcl.expectedMessageCount(1);
        mockedEDcl.assertIsSatisfied();

        //assert datatype, isExternal (?), toShare
        List<Exchange> list = mockedEDcl.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            cspIdFound = data.getDataParams().getCspId();
            assertThat(data.getDataType(), is(integrationDataType));
            //assertThat(data.getSharingParams().getIsExternal(), is(false));
            assertThat(data.getSharingParams().getToShare(), is(true));
        }

        List<String> authorizedCentralCspIdsList = Arrays.asList(IntegrationDataType.authorizedCentralCspIds);
        String finalCspIdFound = cspIdFound;
        boolean notCentralTc = integrationDataType.equals(IntegrationDataType.TRUSTCIRCLE)
                && !authorizedCentralCspIdsList.stream().anyMatch(c->c.equalsIgnoreCase(finalCspIdFound));
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
            assertThat(data.getDataType(), is(integrationDataType));
            assertThat(data.getSharingParams().getIsExternal(), is(false));//this was true once uppon a time, due to the fact that the connection from the controller was synchromized, thus resulting in a synced blocking camel exchange. Since we changed to async, this flag is false, as it supposed to be
            assertThat(data.getSharingParams().getToShare(), is(true));
        }

       /*
        DSL: expect 1-message
         */
        mockedDsl.expectedMessageCount(notCentralTc?0:1);
        mockedDsl.assertIsSatisfied();


        list = mockedDsl.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(integrationDataType));
            assertThat(data.getSharingParams().getIsExternal(), is(true));
            assertThat(data.getSharingParams().getToShare(), is(true));
        }

        /*
        APP
         */
        // The data type to test is defined in class -> private IntegrationDataType dataTypeToTest = IntegrationDataType.VULNERABILITY;
        // The application id is "taranis"
        // Expect 1-messages according to application.properties (external.vulnerability.apps:taranis)
        mockedApp.expectedMessageCount(notCentralTc?0:1);
        mockedApp.assertIsSatisfied();

        list = mockedApp.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(integrationDataType));
            assertThat(data.getSharingParams().getIsExternal(), is(true));
            assertThat(data.getSharingParams().getToShare(), is(true));
        }

        if(notCentralTc) {
            String output = this.outputCapture.toString();
            assertThat(output, containsString(
                    String.format("TC dataType change request received from external CSP (flow2) and is not %s. Csp tried to do this is: %s"
                            ,authorizedCentralCspIdsList.toString(),cspIdFound)
            ));
        }
    }
}
