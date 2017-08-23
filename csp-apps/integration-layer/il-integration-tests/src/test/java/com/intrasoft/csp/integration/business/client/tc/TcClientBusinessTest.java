package com.intrasoft.csp.integration.business.client.tc;

import com.intrasoft.csp.client.TrustCirclesClient;
import com.intrasoft.csp.client.config.CspClientConfig;
import com.intrasoft.csp.client.config.TrustCirclesClientConfig;
import com.intrasoft.csp.commons.model.TrustCircle;
import com.intrasoft.csp.libraries.restclient.config.RestTemplateConfiguration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.ResourceAccessException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

/**
 * Created by iskitsas on 4/10/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {TrustCirclesClient.class, TrustCirclesClientConfig.class, CspClientConfig.class},
        properties = {
                "csp.retry.backOffPeriod:10",//ms
                "csp.retry.maxAttempts:1",
                "tc.protocol:http",
                "tc.host:localhost",
                "tc.port:8081"})
/**
 * In order to run this test, start the node js dummy server:
 * eg.
 * $ APP_NAME=tc PORT=8081 node server.js
 * */
public class TcClientBusinessTest {
    private static final Logger LOG = LoggerFactory.getLogger(TcClientBusinessTest.class);
    @Autowired
    @Qualifier(value = "trustCirclesClient")
    TrustCirclesClient tcClient;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * 3 ways to test expected exceptions
     * */

    @Test
    public void getCspsDataBadRequestTestWay1(){
        exception.expect(ResourceAccessException.class);
        tcClient.setProtocolHostPort("http","dummy","9999");
        tcClient.getTrustCircle(1);
    }

    @Test(expected = ResourceAccessException.class)
    public void getCspsDataBadRequestTestWay2(){
        tcClient.setProtocolHostPort("http","dummy","9999");
        tcClient.getTrustCircle(1);
    }

    @Test
    public void getCspsDataBadRequestTestWay3(){
        try {
            tcClient.setProtocolHostPort("http","dummy","9999");
            tcClient.getTrustCircle(1);
            fail("Expected ResourceAccessException Exception");
        } catch (ResourceAccessException e) {
            assertThat(e.getMessage(),containsString("I/O error on GET request"));
        }
    }

    @Test
    public void getTrustCircleTest(){
        tcClient.setProtocolHostPort("http","csp.dangerduck.gr","8000");
        TrustCircle trustCircle = tcClient.getTrustCircle(1);
        LOG.info(trustCircle.toString());
        assertThat(trustCircle.getTeams().size(),is(2));
    }
}
