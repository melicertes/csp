package com.intrasoft.csp.integration.sandbox.client.tc;

import com.intrasoft.csp.client.TrustCirclesClient;
import com.intrasoft.csp.commons.model.TrustCircle;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.server.CspApp;
import com.intrasoft.csp.server.utils.MockUtils;
import com.intrasoft.csp.server.utils.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Created by iskitsas on 4/10/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {CspApp.class,MockUtils.class},
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

    @Autowired
    Environment env;

    private String trustCirclesContext;

    @Before
    public void init(){
        String trustCirclePath = env.getProperty("tc.path.circles");
        trustCirclesContext = tcClient.getContext()+ trustCirclePath+"/1";
    }

    @Test
    public void getExternalCspsTest() throws IOException {
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(retryRestTemplate).build();
        mockServer.expect(requestTo(trustCirclesContext))
                .andRespond(withSuccess(TestUtil.convertObjectToJsonBytes(mockUtils.getMockedTrustCircle(14)), TestUtil.APPLICATION_JSON_UTF8));

        TrustCircle trustCircle = tcClient.getTrustCircle(1);
        assertThat(trustCircle.getTeams().size(), is(14));

        mockServer.verify();
    }
}
