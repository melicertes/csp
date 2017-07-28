package com.intrasoft.csp.integration.sandbox.client.anon;

import com.intrasoft.csp.anon.AnonApp;
import com.intrasoft.csp.client.config.AnonClientConfig;
import com.intrasoft.csp.client.config.CspRestTemplateConfiguration;
import com.intrasoft.csp.commons.client.ApiVersionClient;
import com.intrasoft.csp.commons.model.VersionDTO;
import com.intrasoft.csp.integration.sandbox.client.csp.CspClientTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AnonApp.class, AnonClientConfig.class, CspRestTemplateConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "server.port: 8585",
                "anon.server.protocol: http",
                "anon.server.host: localhost",
                "anon.server.port: 8585",
                "api.version: 1",
                "csp.retry.backOffPeriod:10",
                "csp.retry.maxAttempts:1",
                "key.update=10000"
        })
@ActiveProfiles("mysql")
public class AnonClientApiVersionTest {
    private static final Logger LOG = LoggerFactory.getLogger(AnonClientApiVersionTest.class);

    @Autowired
    ApiVersionClient apiVersionClient;

    @Test
    public void cspApiVersionTest(){
        VersionDTO versionDTO = apiVersionClient.getApiVersion();
        String apiUrl = apiVersionClient.getApiUrl();
        String anonContext = apiVersionClient.getContext();
        assertThat(versionDTO.getMaxVersion(),is(1.0));
        assertThat(apiUrl,is("http://localhost:8585/v1"));
        assertThat(anonContext,is("/v1"));
    }
}
