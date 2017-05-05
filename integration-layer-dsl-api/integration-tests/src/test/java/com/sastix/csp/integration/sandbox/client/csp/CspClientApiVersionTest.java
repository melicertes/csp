package com.sastix.csp.integration.sandbox.client.csp;

import com.sastix.csp.client.CspClient;
import com.sastix.csp.commons.client.ApiVersionClient;
import com.sastix.csp.commons.client.RetryRestTemplate;
import com.sastix.csp.commons.model.VersionDTO;
import com.sastix.csp.server.CspApp;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by iskitsas on 5/5/17.
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
public class CspClientApiVersionTest {
    private static final Logger LOG = LoggerFactory.getLogger(CspClientTest.class);

    @Autowired
    ApiVersionClient apiVersionClient;

    @Test
    public void cspApiVersionTest(){
        VersionDTO versionDTO = apiVersionClient.getApiVersion();
        String apiUrl = apiVersionClient.getApiUrl();
        String cspContext = apiVersionClient.getContext();
        assertThat(versionDTO.getMaxVersion(),is(1.0));
        assertThat(apiUrl,is("http://localhost:8082/v1"));
        assertThat(cspContext,is("/v1"));
    }
}
