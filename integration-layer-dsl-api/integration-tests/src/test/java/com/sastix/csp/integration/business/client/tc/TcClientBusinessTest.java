package com.sastix.csp.integration.business.client.tc;

import com.sastix.csp.client.TrustCirclesClient;
import com.sastix.csp.client.config.CspRestTemplateConfiguration;
import com.sastix.csp.client.config.TrustCirclesClientConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

/**
 * Created by iskitsas on 4/10/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {TrustCirclesClient.class, CspRestTemplateConfiguration.class, TrustCirclesClientConfig.class},
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
        tcClient.getCsps("localhost");
    }

    @Test(expected = ResourceAccessException.class)
    public void getCspsDataBadRequestTestWay2(){
        tcClient.setProtocolHostPort("http","dummy","9999");
        tcClient.getCsps("dummy");
    }

    @Test
    public void getCspsDataBadRequestTestWay3(){
        try {
            tcClient.setProtocolHostPort("http","dummy","9999");
            tcClient.getCsps("dummy");
            fail("Expected ResourceAccessException Exception");
        } catch (ResourceAccessException e) {
            assertThat(e.getMessage(),containsString("I/O error on POST request"));
        }
    }

    @Test
    public void getCspsTest(){
        List<String> list = tcClient.getCsps("localhost");
        assertThat(list.size(),is(1));
        assertThat(list.get(0),containsString("http://ex.csp1.com"));
    }
}
