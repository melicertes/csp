package com.sastix.csp.integration.sandbox.server.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sastix.csp.client.TrustCirclesClient;
import com.sastix.csp.commons.model.*;
import com.sastix.csp.commons.routes.CamelRoutes;
import com.sastix.csp.integration.MockUtils;
import com.sastix.csp.integration.TestUtil;
import com.sastix.csp.server.IntegrationLayerDslApiApplication;
import com.sastix.csp.server.routes.RouteUtils;
import org.apache.camel.*;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by iskitsas on 4/7/17.
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {IntegrationLayerDslApiApplication.class, MockUtils.class},
        properties = {
                "csp.retry.backOffPeriod:10",
                "csp.retry.maxAttempts:1",
                "embedded.activemq.start:false",
                "apache.camel.use.activemq:false",
        })
@MockEndpointsAndSkip("http:*")
public class CspServerInternalSandboxTest implements CamelRoutes{
    private static final Logger LOG = LoggerFactory.getLogger(CspServerInternalSandboxTest.class);

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TrustCirclesClient tcClient;

    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+DSL)
    private MockEndpoint mockedDsl;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+DDL)
    private MockEndpoint mockedDdl;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+TC)
    private MockEndpoint mockedTC;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+TCT)
    private MockEndpoint mockedTCT;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+ECSP)
    private MockEndpoint mockedEcsp;

    @Autowired
    MockUtils mockUtils;

    @Autowired
    RouteUtils routes;

    @Autowired
    SpringCamelContext springCamelContext;

    private Integer numOfCsps = 3;
    private Integer currentCspId = 0;

    @Before
    public void init() throws Exception {
        mvc = webAppContextSetup(webApplicationContext).build();
        mockUtils.setSpringCamelContext(springCamelContext);
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX,routes.apply(DSL),mockedDsl.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX,routes.apply(DDL), mockedDdl.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX,routes.apply(ECSP), mockedEcsp.getEndpointUri());
        mockUtils.mockRouteSkipSendToOriginalEndpoint(CamelRoutes.MOCK_PREFIX, routes.apply(TC),mockedTC.getEndpointUri());
        mockUtils.mockRouteSkipSendToOriginalEndpoint(CamelRoutes.MOCK_PREFIX,routes.apply(TCT),mockedTCT.getEndpointUri());
    }

    // Use @DirtiesContext on each test method to force Spring Testing to automatically reload the CamelContext after
    // each test method - this ensures that the tests don't clash with each other, e.g., one test method sending to an
    // endpoint that is then reused in another test method.
    @DirtiesContext
    @Test
    public void dslFlow1TestUsingCamelEndpoint() throws Exception {
        mockedTC.returnReplyBody(new Expression() {
            @Override
            public <T> T evaluate(Exchange exchange, Class<T> type) {
                try {
                    return (T) TestUtil.convertObjectToJsonBytes(mockUtils.getMockedTrustCircle(3));
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });

        mockedTC.expectedMessagesMatches(new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                String in = exchange.getIn().getBody(String.class);
                TrustCircle tc = null;
                try {
                    tc = objectMapper.readValue(in, TrustCircle.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assertThat(tc.getCsps().size(),is(3));
                return true;
            }
        });

        mockedTCT.returnReplyBody(new Expression() {
            @Override
            public <T> T evaluate(Exchange exchange, Class<T> type) {
                try {
                    currentCspId = currentCspId+1;
                    return (T) TestUtil.convertObjectToJsonBytes(mockUtils.getMockedTeam(currentCspId,"http://external.csp%s.com"));
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });

        mockUtils.sendFlow1IntegrationData(mvc,false);

        mockedDsl.expectedMessageCount(1);
        mockedDsl.assertIsSatisfied();

        List<Exchange> list = mockedDsl.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(IntegrationDataType.INCIDENT));
        }

        mockedEcsp.expectedMessageCount(1);
        list = mockedEcsp.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            TrustCircleEcspDTO trustCircleEcspDTO = in.getBody(TrustCircleEcspDTO.class);
            List<Team> data = trustCircleEcspDTO.getTeams();
            assertThat(data.size(), is(3));
            assertThat(data.get(0).getUrl(), is("http://external.csp1.com"));
        }

        mockedDdl.expectedMessageCount(1);
        mockedDdl.assertIsSatisfied();
    }
}
