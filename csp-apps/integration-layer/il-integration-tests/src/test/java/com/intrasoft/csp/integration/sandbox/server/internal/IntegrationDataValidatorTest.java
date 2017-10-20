package com.intrasoft.csp.integration.sandbox.server.internal;

import com.intrasoft.csp.commons.apiHttpStatusResponse.HttpStatusResponseType;
import com.intrasoft.csp.commons.routes.ContextUrl;
import com.intrasoft.csp.commons.validators.IntegrationDataValidator;
import com.intrasoft.csp.server.CspApp;
import com.intrasoft.csp.server.utils.TestUtil;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by iskitsas on 6/13/17.
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {CspApp.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "spring.datasource.url:jdbc:h2:mem:csp_policy",
                "flyway.enabled:false",
                "server.port: 8089",
                "csp.server.protocol: http",
                "csp.server.host: localhost",
                "csp.server.port: 8089",
                "api.version: 1",
                "csp.retry.backOffPeriod:10",
                "csp.retry.maxAttempts:1",
                "embedded.activemq.start:false",
                "apache.camel.use.activemq:false",
                "external.use.ssl:false",
                "internal.use.ssl:false",
                "misp.protocol:http",
                "taranis.protocol:http",
        })
@MockEndpointsAndSkip("http:*")
public class IntegrationDataValidatorTest implements ContextUrl {
    @Autowired
    IntegrationDataValidator integrationDataValidator;

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    URL incident_data_example1 = getClass().getClassLoader().getResource("json/incident_data_example1.json");
    URL null_datatype = getClass().getClassLoader().getResource("json/null_datatype.json");
    URL missing_integration_data = getClass().getClassLoader().getResource("json/missing_integration_data.json");
    URL integration_data_vulnerability = getClass().getClassLoader().getResource("json/integration_data_vulnerability.json");
    URL invalid_integration_data_vulnerability = getClass().getClassLoader().getResource("json/invalid_integration_data_vulnerability.json");

    @Before
    public void init() throws Exception {
        mvc = webAppContextSetup(webApplicationContext).build();

    }

    @Test
    public void validIntegrationDataTest() throws Exception {
        String json = FileUtils.readFileToString(new File(integration_data_vulnerability.toURI()),Charset.forName("UTF-8"));
        mvc.perform(post("/v"+REST_API_V1+"/"+DSL_INTEGRATION_DATA).accept(MediaType.TEXT_PLAIN)
                .content(json)
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().string(HttpStatusResponseType.SUCCESSFUL_OPERATION.getReasonPhrase()));
    }

    @Test
    public void validIntegrationDataExample1Test() throws Exception {
        String json = FileUtils.readFileToString(new File(incident_data_example1.toURI()),Charset.forName("UTF-8"));
        mvc.perform(post("/v"+REST_API_V1+"/"+DSL_INTEGRATION_DATA).accept(MediaType.TEXT_PLAIN)
                .content(json)
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().string(HttpStatusResponseType.SUCCESSFUL_OPERATION.getReasonPhrase()));
    }

    @Test
    public void invalidIntegrationDataTest() throws Exception {
        String json = FileUtils.readFileToString(new File(invalid_integration_data_vulnerability.toURI()),Charset.forName("UTF-8"));
        ResultActions resultActions = mvc.perform(post("/v"+REST_API_V1+"/"+DSL_INTEGRATION_DATA).accept(MediaType.TEXT_PLAIN)
                .content(json)
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().is4xxClientError())
                .andExpect(status().reason(HttpStatusResponseType.MALFORMED_INTEGRATION_DATA_STRUCTURE.getReasonPhrase()));
    }


    @Test
    public void missingIntegrationDataTest() throws Exception {
        String json = FileUtils.readFileToString(new File(missing_integration_data.toURI()),Charset.forName("UTF-8"));
        mvc.perform(post("/v"+REST_API_V1+"/"+DSL_INTEGRATION_DATA).accept(MediaType.TEXT_PLAIN)
                .content(json)
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().is4xxClientError())
                .andExpect(status().reason(containsString(HttpStatusResponseType.MALFORMED_INTEGRATION_DATA_STRUCTURE.getReasonPhrase())));
    }

    @Test
    public void nullDataTypeTest() throws Exception {
        String json = FileUtils.readFileToString(new File(null_datatype.toURI()),Charset.forName("UTF-8"));
        mvc.perform(post("/v"+REST_API_V1+"/"+DSL_INTEGRATION_DATA).accept(MediaType.TEXT_PLAIN)
                .content(json)
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().is4xxClientError())
                .andExpect(status().reason(containsString(HttpStatusResponseType.MALFORMED_INTEGRATION_DATA_STRUCTURE.getReasonPhrase())));
    }
}
