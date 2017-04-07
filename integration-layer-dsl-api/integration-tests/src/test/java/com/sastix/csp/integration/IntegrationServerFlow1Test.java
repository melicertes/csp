package com.sastix.csp.integration;

import com.sastix.csp.commons.apiHttpStatusResponse.HttpStatusResponseType;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.IntegrationDataType;
import com.sastix.csp.commons.model.SharingParams;
import com.sastix.csp.commons.routes.CamelRoutes;
import com.sastix.csp.commons.routes.ContextUrl;
import com.sastix.csp.server.IntegrationLayerDslApiApplication;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by iskitsas on 4/7/17.
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = IntegrationLayerDslApiApplication.class)
@MockEndpointsAndSkip("http:*")
public class IntegrationServerFlow1Test {
    private static final Logger LOG = LoggerFactory.getLogger(IntegrationServerFlow1Test.class);

    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+CamelRoutes.DSL)
    private MockEndpoint mockedDsl;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+CamelRoutes.DDL)
    private MockEndpoint mockedDdl;

    @Autowired
    SpringCamelContext springCamelContext;

    @Before
    public void init() throws Exception {
        mvc = webAppContextSetup(webApplicationContext).build();
        mockRoute(CamelRoutes.MOCK_PREFIX,CamelRoutes.DSL);
        mockRoute(CamelRoutes.MOCK_PREFIX,CamelRoutes.DDL);
    }

    // Use @DirtiesContext on the test methods to force Spring Testing to automatically reload the CamelContext after
    // each test method - this ensures that the tests don't clash with each other, e.g., one test method sending to an
    // endpoint that is then reused in another test method.
    @DirtiesContext
    @Test
    public void flow1DslTest() throws Exception {
        sendFlow1IntegrationData(false);

        mockedDsl.expectedMessageCount(1);
        mockedDsl.assertIsSatisfied();

        List<Exchange> list = mockedDsl.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(IntegrationDataType.INCIDENT));
        }
    }

    @DirtiesContext
    @Test
    public void flow1DdlTest() throws Exception {
        sendFlow1IntegrationData(false);
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
                // intercept sending to direct:dsl and do something else
                interceptSendToEndpoint(uri)
                        //.skipSendToOriginalEndpoint()
                        //.to("log:foo")
                        .to(mockPrefix+":"+uri);
            }
        });
    }
}
