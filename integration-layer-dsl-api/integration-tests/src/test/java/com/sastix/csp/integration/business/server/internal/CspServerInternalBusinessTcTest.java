package com.sastix.csp.integration.business.server.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sastix.csp.client.TrustCirclesClient;
import com.sastix.csp.commons.model.Csp;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.IntegrationDataType;
import com.sastix.csp.commons.routes.CamelRoutes;
import com.sastix.csp.integration.MockUtils;
import com.sastix.csp.server.IntegrationLayerDslApiApplication;
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

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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
@MockEndpointsAndSkip("^http://localhost.*adapter.*|http://csp.*|http://ex.*") // by removing this any http requests will be sent as expected.
// In this test we mock all other http requests except for tc. TC dummy server is expected on 3001 port.
// To start the TC dummy server:
// $ APP_NAME=tc PORT=8081 node server.js
public class CspServerInternalBusinessTcTest {
    private static final Logger LOG = LoggerFactory.getLogger(CspServerInternalBusinessTcTest.class);

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

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+CamelRoutes.DCL)
    private MockEndpoint mockedDcl;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+CamelRoutes.TC)
    private MockEndpoint mockedTC;

    @Autowired
    MockUtils mockUtils;

    @Autowired
    SpringCamelContext springCamelContext;

    @Before
    public void init() throws Exception {
        mvc = webAppContextSetup(webApplicationContext).build();
        mockUtils.setSpringCamelContext(springCamelContext);
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX,CamelRoutes.DSL);
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX,CamelRoutes.DCL);
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX,CamelRoutes.DDL);
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX,CamelRoutes.TC);
    }

    // Use @DirtiesContext on each test method to force Spring Testing to automatically reload the CamelContext after
    // each test method - this ensures that the tests don't clash with each other, e.g., one test method sending to an
    // endpoint that is then reused in another test method.
    @DirtiesContext
    @Test
    public void dslFlow1TestUsingCamelEndpoint() throws Exception {
        mockUtils.sendFlow1IntegrationData(mvc,false);


        mockedDsl.expectedMessageCount(1);
        mockedDsl.assertIsSatisfied();

        List<Exchange> list = mockedDsl.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(IntegrationDataType.INCIDENT));
        }

        mockedTC.expectedMessageCount(1);
        list = mockedTC.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            Csp dataIn = in.getBody(Csp.class);
            assertThat(dataIn.getCspId(), containsString("localhost"));
        }

        mockedDcl.expectedMessageCount(1);

        mockedDdl.expectedMessageCount(1);
        mockedDdl.assertIsSatisfied();
    }
}
