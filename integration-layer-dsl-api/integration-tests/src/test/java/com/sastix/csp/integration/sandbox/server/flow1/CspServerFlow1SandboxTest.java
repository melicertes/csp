package com.sastix.csp.integration.sandbox.server.flow1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sastix.csp.client.TrustCirclesClient;
import com.sastix.csp.commons.apiHttpStatusResponse.HttpStatusResponseType;
import com.sastix.csp.commons.client.RetryRestTemplate;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.IntegrationDataType;
import com.sastix.csp.commons.model.SharingParams;
import com.sastix.csp.commons.model.TrustCircle;
import com.sastix.csp.commons.routes.CamelRoutes;
import com.sastix.csp.commons.routes.ContextUrl;
import com.sastix.csp.integration.MockUtils;
import com.sastix.csp.integration.TestUtil;
import com.sastix.csp.server.IntegrationLayerDslApiApplication;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by iskitsas on 4/7/17.
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {IntegrationLayerDslApiApplication.class, MockUtils.class},
        properties = {
                "csp.retry.backOffPeriod:10",
                "csp.retry.maxAttempts:1"
        })
@MockEndpointsAndSkip("http:*")
public class CspServerFlow1SandboxTest {
    private static final Logger LOG = LoggerFactory.getLogger(CspServerFlow1SandboxTest.class);

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TrustCirclesClient tcClient;

    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+CamelRoutes.DSL)
    private MockEndpoint mockedDsl;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+CamelRoutes.DDL)
    private MockEndpoint mockedDdl;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+CamelRoutes.TC)
    private MockEndpoint mockedTC;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+CamelRoutes.ECSP)
    private MockEndpoint mockedEcsp;

    @Autowired
    MockUtils mockUtils;

    @Autowired
    SpringCamelContext springCamelContext;

    @Before
    public void init() throws Exception {
        mvc = webAppContextSetup(webApplicationContext).build();
        mockUtils.setSpringCamelContext(springCamelContext);
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX,CamelRoutes.DSL);
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX,CamelRoutes.DDL);
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX,CamelRoutes.ECSP);
        mockUtils.mockRouteSkipSendToOriginalEndpoint(CamelRoutes.MOCK_PREFIX,CamelRoutes.TC);
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
                    return (T) TestUtil.convertObjectToJsonBytes(mockUtils.getMockedTrustCircle(3,"http://external.csp%s.com"));
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
            TrustCircle data = in.getBody(TrustCircle.class);
            assertThat(data.getCsps().size(), is(3));
            assertThat(data.getCsps().get(0), is("http://external.csp1.com"));
        }

        mockedDdl.expectedMessageCount(1);
        mockedDdl.assertIsSatisfied();
    }
}
