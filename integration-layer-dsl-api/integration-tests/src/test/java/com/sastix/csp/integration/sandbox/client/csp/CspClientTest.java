package com.sastix.csp.integration.sandbox.client.csp;

import com.sastix.csp.client.CspClient;
import com.sastix.csp.commons.client.ApiVersionClient;
import com.sastix.csp.commons.client.ApiVersionClientImpl;
import com.sastix.csp.commons.client.RetryRestTemplate;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.IntegrationDataType;
import com.sastix.csp.commons.model.SharingParams;
import com.sastix.csp.commons.model.VersionDTO;
import com.sastix.csp.commons.routes.ContextUrl;
import com.sastix.csp.integration.MockUtils;
import com.sastix.csp.integration.TestUtil;
import com.sastix.csp.integration.sandbox.server.internal.CspServerInternalSandboxTest;
import com.sastix.csp.server.CspApp;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Created by iskitsas on 5/3/17.
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {CspApp.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "server.port: 8082",
                "csp.server.protocol: http",
                "csp.server.host: localhost",
                "csp.server.port: 8082",
                "api.version: 1",
                "csp.retry.backOffPeriod:10",
                "csp.retry.maxAttempts:1",
                "embedded.activemq.start:false",
                "apache.camel.use.activemq:false",
        })
@MockEndpointsAndSkip("http:*")
public class CspClientTest implements ContextUrl {
    private static final Logger LOG = LoggerFactory.getLogger(CspClientTest.class);

    @Autowired
    ApiVersionClient apiVersionClient;

    @Autowired
    RetryRestTemplate retryRestTemplate;

    @Autowired
    CspClient cspClient;

    @Test
    public void sendPostIntegrationDataTest() throws IOException {
        String apiUrl = apiVersionClient.getApiUrl();
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(retryRestTemplate).build();
        mockServer.expect(requestTo(apiUrl+ DSL_INTEGRATION_DATA))
                .andRespond(withSuccess(TestUtil.convertObjectToJsonBytes(new ResponseEntity<>("Successful", HttpStatus.OK)),TestUtil.APPLICATION_JSON_UTF8));

        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataType(IntegrationDataType.INCIDENT);
        SharingParams sharingParams = new SharingParams();
        sharingParams.setIsExternal(false);
        sharingParams.setToShare(true);
        integrationData.setSharingParams(sharingParams);
        ResponseEntity<String> response = cspClient.postIntegrationData(integrationData, DSL_INTEGRATION_DATA);
        assertThat(response.getStatusCode(),is(HttpStatus.OK));

        mockServer.verify();
    }
}



