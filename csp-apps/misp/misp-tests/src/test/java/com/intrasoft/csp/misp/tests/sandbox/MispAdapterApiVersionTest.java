
package com.intrasoft.csp.misp.tests.sandbox;

import com.intrasoft.csp.libraries.versioning.client.ApiVersionClient;
import com.intrasoft.csp.libraries.versioning.model.VersionDTO;
import com.intrasoft.csp.misp.MispAdapterEmitterApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MispAdapterEmitterApplication.class, ApiVersionClient.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "server.name:LOCAL-CERT",
                "server.port: 8585",
                "adapter.server.protocol: http",
                "adapter.server.host: localhost",
                "adapter.server.port: 8585",
                "api.version: 1",
                "csp.retry.backOffPeriod:10",
                "csp.retry.maxAttempts:1",
                "key.update=10000",
                "enable.oam:false",
                "elastic.protocol:http",
                "elastic.host:csp0.dangerduck.gr",
                "elastic.port:9200",
                "elastic.path:",
                "zeromq.protocol=tcp",
        "zeromq.host=localhost",
        "zeromq.port=50000",
        "zeromq.topic=misp_json"
        })

public class MispAdapterApiVersionTest {
    private static final Logger LOG = LoggerFactory.getLogger(MispAdapterApiVersionTest.class);

    @Autowired
    @Qualifier("MispApiVersionClient")
    ApiVersionClient apiVersionClient;

    @Test
    public void mispApiVersionTest(){
        VersionDTO versionDTO = apiVersionClient.getApiVersion();
        String apiUrl = apiVersionClient.getApiUrl();
        String anonContext = apiVersionClient.getContext();
        assertThat(versionDTO.getMaxVersion(),is(1.0));
        assertThat(apiUrl,is("http://localhost:8585/v1"));
        assertThat(anonContext,is("/v1"));
    }
}

