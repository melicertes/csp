package com.intrasoft.csp.integration.business.server.internal;


import com.intrasoft.csp.commons.model.*;
import com.intrasoft.csp.commons.routes.CamelRoutes;
import com.intrasoft.csp.server.CspApp;
import com.intrasoft.csp.server.policy.domain.model.EvaluatedPolicyDTO;
import com.intrasoft.csp.server.policy.domain.model.PolicyDTO;
import com.intrasoft.csp.server.policy.domain.model.SharingPolicyAction;
import com.intrasoft.csp.server.policy.service.SharingPolicyService;
import com.intrasoft.csp.server.processors.TcProcessor;
import com.intrasoft.csp.server.routes.RouteUtils;
import com.intrasoft.csp.server.service.CamelRestService;
import com.intrasoft.csp.server.service.ErrorMessageHandler;
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
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {CspApp.class, MockUtils.class},
        properties = {
                "spring.datasource.url:jdbc:h2:mem:csp_policy",
                "flyway.enabled:false",
        /*
        //added in application-dangerduck.properties
                "consume.errorq.on.interval:false",
                "csp.retry.backOffPeriod:10",
                "csp.retry.maxAttempts:1",
                "embedded.activemq.start:false",
                "apache.camel.use.activemq:false",
                "internal.use.ssl: false",
                "internal.ssl.keystore.resource: sslcert/csp-internal.jks",
                "internal.ssl.keystore.passphrase: 123456",
                "external.use.ssl: false",
                "external.ssl.keystore.resource: sslcert/csp-internal.jks",
                "external.ssl.keystore.passphrase: 123456",

                "misp.protocol: http",
                "misp.host: csp2.dangerduck.gr",
                "misp.port: 8082",
                "misp.path: /adapter/misp",

                "viper.protocol: http",
                "viper.host: csp2.dangerduck.gr",
                "viper.port: 8082",
                "viper.path: /adapter/viper",

                "tc.protocol: http",
                "tc.host: tc.csp2.dangerduck.gr",
                "tc.port: 8000",
                "tc.path.circles: /api/v1/circles",
                "tc.path.teams: /api/v1/teams",

                "trustcircle.protocol: http",
                "trustcircle.host: csp2.dangerduck.gr",
                "trustcircle.port: 8082",
                "trustcircle.path: /adapter/tc",

                "elastic.protocol: http",
                "elastic.host: csp2.dangerduck.gr",
                "elastic.port: 9200",
                "elastic.path: /cspdata"
                */
        })
//@MockEndpointsAndSkip("^https4-in://localhost.*adapter.*|https4-in://csp.*|https4-ex://ex.*") // by removing this any http requests will be sent as expected.
//@MockEndpointsAndSkip("http://external.csp*") // by removing this any http requests will be sent as expected.
@MockEndpointsAndSkip("^http.*://.*/v.*/dcl/integrationData")

// In this test we mock all other http requests except for tc. TC dummy server is expected on 3001 port.
// To start the TC dummy server:
// $ APP_NAME=tc SSL=true PORT=8081 node server.js
public class CspServerInternalBusinessTestFlow1dataTypes implements CamelRoutes {

    private static final Logger LOG = LoggerFactory.getLogger(CspServerInternalBusinessTestFlow1verbs.class);

    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX + ":" + DIRECT + ":" + DSL)
    private MockEndpoint mockedDsl;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX + ":" + DIRECT + ":" + APP)
    private MockEndpoint mockedApp;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX + ":" + DIRECT + ":" + DDL)
    private MockEndpoint mockedDdl;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX + ":" + DIRECT + ":" + DCL)
    private MockEndpoint mockedDcl;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX + ":" + DIRECT + ":" + TC)
    private MockEndpoint mockedTC;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX + ":" + DIRECT + ":" + ECSP)
    private MockEndpoint mockedEcsp;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX + ":" + DIRECT + ":" + ELASTIC)
    private MockEndpoint mockedElastic;

    @Autowired
    MockUtils mockUtils;

    @Autowired
    RouteUtils routes;

    @Autowired
    CamelRestService camelRestService;

    @Autowired
    SpringCamelContext springCamelContext;

    @Autowired
    ErrorMessageHandler errorMessageHandler;

    @Autowired
    TcProcessor tcProcessor;

    @MockBean
    SharingPolicyService sharingPolicyService;

    @Autowired
    Environment env;

    private Integer numOfCspsToTest = 3;
    private Integer currentCspId = 0;
    private HashMap<IntegrationDataType, Integer> internalApps = new HashMap<>();

    String serverName;
    String tcProtocol;
    String tcHost;
    String tcPort;
    String tcPathCircles;
    String tcPathTeams;

    String applicationId = "taranis";
    String tcId = "tcId";
    String teamId = "teamId";
    @Before
    public void init() throws Exception {
        String tcIdArg = env.getProperty("extTcId");
        if(!StringUtils.isEmpty(tcIdArg)){
            tcId = tcIdArg;
        }

        String teamIdArg = env.getProperty("extTeamId");
        if(!StringUtils.isEmpty(teamIdArg)){
            teamId = teamIdArg;
        }
        mvc = webAppContextSetup(webApplicationContext).build();
        mockUtils.setSpringCamelContext(springCamelContext);

        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.apply(DSL), mockedDsl.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.apply(APP), mockedApp.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.apply(DDL), mockedDdl.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.apply(DCL), mockedDcl.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.apply(TC), mockedTC.getEndpointUri());
        mockUtils.mockRouteSkipSendToOriginalEndpoint(CamelRoutes.MOCK_PREFIX, routes.apply(ECSP), mockedEcsp.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.apply(ELASTIC), mockedElastic.getEndpointUri());

        //Initialize internalApps Hashmap according application.properties (internal section)
        internalApps.put(IntegrationDataType.THREAT, env.getProperty("internal.threat.apps").split(",").length);
        internalApps.put(IntegrationDataType.ARTEFACT, env.getProperty("internal.artefact.apps").split(",").length);
        internalApps.put(IntegrationDataType.TRUSTCIRCLE, env.getProperty("internal.trustcircle.apps").split(",").length);

        serverName = env.getProperty("server.name");
        tcProtocol=env.getProperty("tc.protocol");
        tcHost=env.getProperty("tc.host");
        tcPort=env.getProperty("tc.port");
        tcPathCircles=env.getProperty("tc.path.circles");
        tcPathTeams=env.getProperty("tc.path.teams");

        EvaluatedPolicyDTO evaluatedPolicyDTO = new EvaluatedPolicyDTO();
        evaluatedPolicyDTO.setSharingPolicyAction(SharingPolicyAction.NO_ACTION_FOUND);
        PolicyDTO mockedPolicyDTO = new PolicyDTO();
        evaluatedPolicyDTO.setPolicyDTO(mockedPolicyDTO);
        Mockito.when(sharingPolicyService.evaluate(anyObject(),anyObject())).thenReturn(evaluatedPolicyDTO);
    }


    // Use @DirtiesContext on each test method to force Spring Testing to automatically reload the CamelContext after
    // each test method - this ensures that the tests don't clash with each other, e.g., one test method sending to an
    // endpoint that is then reused in another test method.
    @DirtiesContext
    @Test
    public void dslFlow1PostDataTypeThreatTest() throws Exception {
        mockUtils.sendFlow1Data(mvc, serverName,applicationId,false, true, IntegrationDataType.THREAT, HttpMethods.POST.name());
        assertFlows(IntegrationDataType.THREAT, tcProcessor.getTcTeams(IntegrationDataType.THREAT).size());
    }

    @DirtiesContext
    @Test
    public void dslFlow1PostTcIdThreatTest() throws Exception {
        mockUtils.sendFlow1Data(mvc, serverName,applicationId,tcId,null,false, true, IntegrationDataType.THREAT, HttpMethods.POST.name());
        assertFlows(IntegrationDataType.THREAT, tcProcessor.getTcTeams(IntegrationDataType.THREAT).size());
    }

    @DirtiesContext
    @Test
    public void dslFlow1PostTeamIdThreatTest() throws Exception {
        mockUtils.sendFlow1Data(mvc, serverName,applicationId,null,teamId,false, true, IntegrationDataType.THREAT, HttpMethods.POST.name());
        assertFlows(IntegrationDataType.THREAT, 1);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PutDataTypeThreatTest() throws Exception {
        mockUtils.sendFlow1Data(mvc, serverName,applicationId,false, true, IntegrationDataType.THREAT, HttpMethods.PUT.name());
        assertFlows(IntegrationDataType.THREAT, tcProcessor.getTcTeams(IntegrationDataType.THREAT).size());
    }

    @DirtiesContext
    @Test
    public void dslFlow1PutTcIdThreatTest() throws Exception {
        mockUtils.sendFlow1Data(mvc, serverName,applicationId,tcId,null,false, true, IntegrationDataType.THREAT, HttpMethods.PUT.name());
        assertFlows(IntegrationDataType.THREAT, tcProcessor.getTcTeams(IntegrationDataType.THREAT).size());
    }

    @DirtiesContext
    @Test
    public void dslFlow1PutTeamIdThreatTest() throws Exception {
        mockUtils.sendFlow1Data(mvc, serverName,applicationId,null,teamId,false, true, IntegrationDataType.THREAT, HttpMethods.PUT.name());
        assertFlows(IntegrationDataType.THREAT, 1);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PostDataTypeArtefactTest() throws Exception {
        mockUtils.sendFlow1Data(mvc, serverName, applicationId,false, true, IntegrationDataType.ARTEFACT, HttpMethods.POST.name());
        assertFlows(IntegrationDataType.ARTEFACT, tcProcessor.getTcTeams(IntegrationDataType.ARTEFACT).size());
    }

    @DirtiesContext
    @Test
    public void dslFlow1PosTcIdArtefactTest() throws Exception {
        mockUtils.sendFlow1Data(mvc, serverName, applicationId,tcId,null,false, true, IntegrationDataType.ARTEFACT, HttpMethods.POST.name());
        assertFlows(IntegrationDataType.ARTEFACT, tcProcessor.getTcTeams(IntegrationDataType.ARTEFACT).size());
    }

    @DirtiesContext
    @Test
    public void dslFlow1PostTeamIdArtefactTest() throws Exception {
        mockUtils.sendFlow1Data(mvc, serverName, applicationId,null,teamId,false, true, IntegrationDataType.ARTEFACT, HttpMethods.POST.name());
        assertFlows(IntegrationDataType.ARTEFACT, 1);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PutDataTypeArtefactTest() throws Exception {
        mockUtils.sendFlow1Data(mvc, serverName, applicationId,false, true, IntegrationDataType.ARTEFACT, HttpMethods.PUT.name());
        assertFlows(IntegrationDataType.ARTEFACT, tcProcessor.getTcTeams(IntegrationDataType.ARTEFACT).size());
    }

    @DirtiesContext
    @Test
    public void dslFlow1PutTcIdArtefactTest() throws Exception {
        mockUtils.sendFlow1Data(mvc, serverName, applicationId,tcId,null,false, true, IntegrationDataType.ARTEFACT, HttpMethods.PUT.name());
        assertFlows(IntegrationDataType.ARTEFACT, tcProcessor.getTcTeams(IntegrationDataType.ARTEFACT).size());
    }

    @DirtiesContext
    @Test
    public void dslFlow1PutTeamIdArtefactTest() throws Exception {
        mockUtils.sendFlow1Data(mvc, serverName, applicationId,null,teamId,false, true, IntegrationDataType.ARTEFACT, HttpMethods.PUT.name());
        assertFlows(IntegrationDataType.ARTEFACT, 1);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PostDataTypeTrustcircleTest() throws Exception {
        mockUtils.sendFlow1Data(mvc, serverName, applicationId,false, true, IntegrationDataType.TRUSTCIRCLE, HttpMethods.POST.name());
        assertFlows(IntegrationDataType.TRUSTCIRCLE, tcProcessor.getTcTeams(IntegrationDataType.TRUSTCIRCLE).size());
    }

    @DirtiesContext
    @Test
    public void dslFlow1PosTcIdTrustcircleTest() throws Exception {
        mockUtils.sendFlow1Data(mvc, serverName, applicationId,tcId,null,false, true, IntegrationDataType.TRUSTCIRCLE, HttpMethods.POST.name());
        assertFlows(IntegrationDataType.TRUSTCIRCLE, tcProcessor.getTcTeams(IntegrationDataType.TRUSTCIRCLE).size());
    }

    @DirtiesContext
    @Test
    public void dslFlow1PostTeamIdTrustcircleTest() throws Exception {
        mockUtils.sendFlow1Data(mvc, serverName, applicationId,null,teamId,false, true, IntegrationDataType.TRUSTCIRCLE, HttpMethods.POST.name());
        assertFlows(IntegrationDataType.TRUSTCIRCLE, 1);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PutDataTypeTrustcircleTest() throws Exception {
        mockUtils.sendFlow1Data(mvc,serverName, applicationId,false, true, IntegrationDataType.TRUSTCIRCLE, HttpMethods.PUT.name());
        assertFlows(IntegrationDataType.TRUSTCIRCLE, tcProcessor.getTcTeams(IntegrationDataType.TRUSTCIRCLE).size());
    }

    @DirtiesContext
    @Test
    public void dslFlow1PutTcIdTrustcircleTest() throws Exception {
        mockUtils.sendFlow1Data(mvc,serverName, applicationId,tcId,null,false, true, IntegrationDataType.TRUSTCIRCLE, HttpMethods.PUT.name());
        assertFlows(IntegrationDataType.TRUSTCIRCLE, tcProcessor.getTcTeams(IntegrationDataType.TRUSTCIRCLE).size());
    }

    @DirtiesContext
    @Test
    public void dslFlow1PutTeamIdTrustcircleTest() throws Exception {
        mockUtils.sendFlow1Data(mvc,serverName, applicationId,null,teamId,false, true, IntegrationDataType.TRUSTCIRCLE, HttpMethods.PUT.name());
        assertFlows(IntegrationDataType.TRUSTCIRCLE, 1);
    }

    private void assertFlows(IntegrationDataType dataType, Integer expectedEscpMessages) throws Exception {
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
            assertThat(exchange.getIn().getHeader(CamelRoutes.ORIGIN_ENDPOINT), is(routes.apply(CamelRoutes.DCL)));
        }

        //ESCP
        mockedEcsp.expectedMessageCount(expectedEscpMessages);
        mockedEcsp.assertIsSatisfied();

        list = mockedEcsp.getReceivedExchanges();
        for (Exchange exchange : list) {
            /**
             * @CHECK
            Assertion on Team's url is meaningless for real teams coming from TC
             */
            //Message in = exchange.getIn();
            //EnhancedTeamDTO enhancedTeamDTO = in.getBody(EnhancedTeamDTO.class);
            //assertThat(enhancedTeamDTO.getTeam().getUrl(), is("http://csp2.dangerduck.gr:8081"));
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




    private String getTcCirclesURI() {


        return tcProtocol + "://" + tcHost + ":" + tcPort + tcPathCircles;
    }

    private String getTcTeamsURI() {
        return tcProtocol + "://" + tcHost + ":" + tcPort + tcPathTeams;
    }

}

