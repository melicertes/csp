package com.intrasoft.csp.integration.sandbox.client.csp;

import com.intrasoft.csp.client.CspClient;
import com.intrasoft.csp.client.config.CspClientConfig;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.SharingParams;
import com.intrasoft.csp.commons.routes.ContextUrl;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.libraries.versioning.client.ApiVersionClient;
import com.intrasoft.csp.server.CspApp;
import com.intrasoft.csp.server.utils.TestUtil;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Created by iskitsas on 5/3/17.
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {CspApp.class, CspClientConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "spring.datasource.url:jdbc:h2:mem:csp_policy",
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
    @Qualifier("CspApiVersionClient")
    ApiVersionClient apiVersionClient;

    @Autowired
    @Qualifier("CspRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Autowired
    CspClient cspClient;

    @Test
    public void sendPostIntegrationDataTest() throws IOException {
        String apiUrl = apiVersionClient.getApiUrl();
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(retryRestTemplate).build();
        mockServer.expect(requestTo(apiUrl+ DSL_INTEGRATION_DATA))
                .andRespond(MockRestResponseCreators.withSuccess(TestUtil.convertObjectToJsonBytes(new ResponseEntity<>("Successful", HttpStatus.OK)),TestUtil.APPLICATION_JSON_UTF8));

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



