package com.sastix.csp.integration;

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
import com.sastix.csp.server.CspApp;
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
@Deprecated // see business or sandbox packages for relative examples
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {CspApp.class, MockUtils.class},
        properties = {
                "csp.retry.backOffPeriod:10",
                "csp.retry.maxAttempts:1"
        })
@MockEndpointsAndSkip("http:*")
public class IntegrationServerFlow1Test {
    private static final Logger LOG = LoggerFactory.getLogger(IntegrationServerFlow1Test.class);

    @Autowired
    @Qualifier("CspRestTemplate")
    RetryRestTemplate retryRestTemplate;

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

    @Autowired
    MockUtils mockUtils;

    @Autowired
    SpringCamelContext springCamelContext;

    String trustCirclesContext;

    @Before
    public void init() throws Exception {
        mvc = webAppContextSetup(webApplicationContext).build();
        mockRoute(CamelRoutes.MOCK_PREFIX,CamelRoutes.DSL);
        mockRoute(CamelRoutes.MOCK_PREFIX,CamelRoutes.DDL);
        mockRouteSkipSendToOriginalEndpoint(CamelRoutes.MOCK_PREFIX,CamelRoutes.TC);
        trustCirclesContext = tcClient.getContext()+ContextUrl.TRUST_CIRCLE;
        //MockEndpoint resultEndpoint = springCamelContext.addEndpoint("mock:foo", new MockEndpoint());
    }

    // Use @DirtiesContext on the test methods to force Spring Testing to automatically reload the CamelContext after
    // each test method - this ensures that the tests don't clash with each other, e.g., one test method sending to an
    // endpoint that is then reused in another test method.
    @DirtiesContext
    @Test
    public void flow1DslTestUsingTrustCirclesClient() throws Exception {
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(retryRestTemplate).build();
        mockServer.expect(requestTo(trustCirclesContext)).andRespond(withSuccess(TestUtil.convertObjectToJsonBytes(mockUtils.getMockedTrustCircle(3)),TestUtil.APPLICATION_JSON_UTF8));

        sendFlow1IntegrationData(false);

        mockedDsl.expectedMessageCount(1);
        mockedDsl.assertIsSatisfied();

        List<Exchange> list = mockedDsl.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(IntegrationDataType.INCIDENT));
        }

        mockedDdl.expectedMessageCount(1);
        mockedDdl.assertIsSatisfied();
        mockServer.verify();
    }

    @DirtiesContext
    @Test
    public void flow1DdlTestUsingCamelEndpointOnly() throws Exception {
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

        sendFlow1IntegrationData(false);

        mockedDsl.expectedMessageCount(1);
        mockedDsl.assertIsSatisfied();

        List<Exchange> list = mockedDsl.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(IntegrationDataType.INCIDENT));
        }

        mockedDdl.expectedMessageCount(1);
        mockedDdl.assertIsSatisfied();
    }

    private void sendFlow1IntegrationData(Boolean isExternal) throws Exception {
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

    RouteDefinition getRoute(String uri){
        List<RouteDefinition> list = springCamelContext.getRouteDefinitions();
        return list.stream().filter(r->r.getInputs().stream().anyMatch(i->i.getUri().equalsIgnoreCase(uri))).findFirst().get();
    }

    void mockRoute(String mockPrefix,String uri) throws Exception {
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
    void mockRouteSkipSendToOriginalEndpoint(String mockPrefix,String uri) throws Exception {
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
}
