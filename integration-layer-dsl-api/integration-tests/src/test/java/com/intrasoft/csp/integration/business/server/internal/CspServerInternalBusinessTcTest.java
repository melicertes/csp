package com.intrasoft.csp.integration.business.server.internal;

import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.routes.CamelRoutes;
import com.intrasoft.csp.integration.MockUtils;
import com.intrasoft.csp.server.CspApp;
import com.intrasoft.csp.server.routes.RouteUtils;
import com.intrasoft.csp.server.service.ErrorMessageHandler;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by iskitsas on 4/7/17.
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {CspApp.class, MockUtils.class},
        properties = {
                "csp.retry.backOffPeriod:10",
                "csp.retry.maxAttempts:1",
                "embedded.activemq.start:true",
                "apache.camel.use.activemq:true",
                "internal.use.ssl: true",
                "internal.ssl.keystore.resource: sslcert/csp-internal.jks",
                "internal.ssl.keystore.passphrase: 123456",
                "external.use.ssl: true",
                "external.ssl.keystore.resource: sslcert/csp-internal.jks",
                "external.ssl.keystore.passphrase: 123456",
                "tc.protocol: https4-in",
                "tc.host: localhost",
                "tc.port: 8081",
                "tc.path.circles:/tc",
                "tc.path.teams:/tct"
        })
@MockEndpointsAndSkip("^https4-in://localhost.*adapter.*|https4-in://csp.*|https4-ex://ex.*") // by removing this any http requests will be sent as expected.
// In this test we mock all other http requests except for tc. TC dummy server is expected on 3001 port.
// To start the TC dummy server:
// $ APP_NAME=tc SSL=true PORT=8081 node server.js
public class CspServerInternalBusinessTcTest implements CamelRoutes {
    private static final Logger LOG = LoggerFactory.getLogger(CspServerInternalBusinessTcTest.class);

    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+DSL)
    private MockEndpoint mockedDsl;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+DDL)
    private MockEndpoint mockedDdl;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+DCL)
    private MockEndpoint mockedDcl;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+TC)
    private MockEndpoint mockedTC;

    @Autowired
    MockUtils mockUtils;

    @Autowired
    RouteUtils routes;

    @Autowired
    SpringCamelContext springCamelContext;

    @Autowired
    ErrorMessageHandler errorMessageHandler;

    @Before
    public void init() throws Exception {
        mvc = webAppContextSetup(webApplicationContext).build();
        mockUtils.setSpringCamelContext(springCamelContext);
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX,routes.apply(DSL),mockedDsl.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX,routes.apply(DCL),mockedDcl.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX,routes.apply(DDL),mockedDdl.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX,routes.apply(TC),mockedTC.getEndpointUri());
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
        mockedTC.assertIsSatisfied();
        list = mockedTC.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData dataIn = in.getBody(IntegrationData.class);
            assertThat(dataIn.getDataType(), is(IntegrationDataType.INCIDENT));
            assertThat(exchange.getIn().getHeader(CamelRoutes.ORIGIN_ENDPOINT),is(routes.apply(CamelRoutes.DCL)));
        }

        mockedDcl.expectedMessageCount(1);

        mockedDdl.expectedMessageCount(1);
        mockedDdl.assertIsSatisfied();

        Thread.sleep(10*1000); //to avoid "Rejecting received message because of the listener container having been stopped in the meantime"
        //be careful when debugging, you might miss breakpoints if the time is not enough
        errorMessageHandler.consumeErrorMessages(1, 1000L,"<br/>");
    }
}
