package com.sastix.csp.integration.sandbox.client.tc;

import com.sastix.csp.client.TrustCirclesClient;
import com.sastix.csp.client.config.CspRestTemplateConfiguration;
import com.sastix.csp.client.config.TrustCirclesClientConfig;
import com.sastix.csp.commons.client.RetryRestTemplate;
import com.sastix.csp.commons.routes.ContextUrl;
import com.sastix.csp.integration.MockUtils;
import com.sastix.csp.integration.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Created by iskitsas on 4/10/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {TrustCirclesClient.class, CspRestTemplateConfiguration.class, TrustCirclesClientConfig.class, MockUtils.class},
        properties = {
                "csp.retry.backOffPeriod:10",
                "csp.retry.maxAttempts:1"
        })
public class TcClientSandboxTest {
    @Autowired
    TrustCirclesClient tcClient;

    @Autowired
    @Qualifier("CspRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Autowired
    MockUtils mockUtils;

    private String trustCirclesContext;

    @Before
    public void init(){
        trustCirclesContext = tcClient.getContext()+ ContextUrl.TRUST_CIRCLE;
    }

    @Test
    public void getExternalCspsTest() throws IOException {
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(retryRestTemplate).build();
        mockServer.expect(requestTo(trustCirclesContext))
                .andRespond(withSuccess(TestUtil.convertObjectToJsonBytes(mockUtils.getMockedTrustCircle(14,"http://external.csp%s.com")),TestUtil.APPLICATION_JSON_UTF8));

        List<String> ecsps = tcClient.getCsps("localhost");
        assertThat(ecsps.size(), is(14));
        ecsps.forEach(str->{
            assertThat(str, containsString("http://external.csp"));
        });

        mockServer.verify();
    }
}
