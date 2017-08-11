package com.intrasoft.csp.conf.integrationtests.sandbox.client;

import com.intrasoft.csp.conf.client.config.ConfClientConfig;
import com.intrasoft.csp.conf.server.ConfApp;
import com.intrasoft.csp.libraries.restclient.config.CspRestTemplateConfiguration;
import com.intrasoft.csp.libraries.versioning.client.ApiVersionClient;
import com.intrasoft.csp.libraries.versioning.model.VersionDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ConfApp.class, ConfClientConfig.class, CspRestTemplateConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "server.port: 8090",
                "conf.server.protocol: http",
                "conf.server.host: localhost",
                "conf.server.port: 8090",
                "api.version: 1",
                "csp.retry.backOffPeriod:10",
                "csp.retry.maxAttempts:1",
                "key.update=10000"
        })
@ActiveProfiles("postgres")
public class ConfClientApiVersionTest {
    private static final Logger LOG = LoggerFactory.getLogger(ConfClientApiVersionTest.class);

    @Autowired
    @Qualifier("ConfApiVersionClient")
    ApiVersionClient apiVersionClient;

    @Test
    public void cspApiVersionTest(){
        VersionDTO versionDTO = apiVersionClient.getApiVersion();
        String apiUrl = apiVersionClient.getApiUrl();
        String anonContext = apiVersionClient.getContext();
        assertThat(versionDTO.getMaxVersion(),is(1.0));
        assertThat(apiUrl,is("http://localhost:8090/v1"));
        assertThat(anonContext,is("/v1"));
    }
}
