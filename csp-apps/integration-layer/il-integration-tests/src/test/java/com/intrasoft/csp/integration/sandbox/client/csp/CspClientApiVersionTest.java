package com.intrasoft.csp.integration.sandbox.client.csp;

import com.intrasoft.csp.client.config.CspClientConfig;
import com.intrasoft.csp.libraries.versioning.client.ApiVersionClient;
import com.intrasoft.csp.libraries.versioning.model.VersionDTO;
import com.intrasoft.csp.server.CspApp;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by iskitsas on 5/5/17.
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {CspApp.class, CspClientConfig.class},
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
    @Qualifier("CspApiVersionClient")
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
