package com.sastix.csp.integration.business.client.adapter;

import com.sastix.csp.client.AdapterClient;
import com.sastix.csp.client.config.AdapterClientConfig;
import com.sastix.csp.client.config.CspRestTemplateConfiguration;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.IntegrationDataType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

/**
 * Created by iskitsas on 4/6/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {AdapterClient.class, CspRestTemplateConfiguration.class, AdapterClientConfig.class},
        properties = {
                "csp.retry.backOffPeriod:10",
                "csp.retry.maxAttempts:1",
                "adapter.server.protocol:http",
                "adapter.server.host:localhost",
                "adapter.server.port:3001"})
/**
 * In order to run this test, start the node js dummy server:
 * eg.
 * $ APP_NAME=adapter PORT=3001 node server.js
 * */
public class AdapterClientBusinessTest {
    @Autowired
    @Qualifier(value = "adapterClient")
    AdapterClient adapterClient;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * 3 ways to test expected exceptions
     * */

    @Test
    public void processNewIntegrationDataBadRequestTestWay1(){
        IntegrationData integrationDataEmpty = new IntegrationData();
        exception.expect(HttpClientErrorException.class);
        adapterClient.processNewIntegrationData(integrationDataEmpty);
    }

    @Test(expected = HttpClientErrorException.class)
    public void processNewIntegrationDataBadRequestTestWay2(){
        IntegrationData integrationDataEmpty = new IntegrationData();
        adapterClient.processNewIntegrationData(integrationDataEmpty);
    }

    @Test
    public void processNewIntegrationDataBadRequestTestWay3(){
        IntegrationData integrationDataEmpty = new IntegrationData();
        try {
            adapterClient.processNewIntegrationData(integrationDataEmpty);
            fail("Expected HttpClientErrorException Exception");
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode(),is(HttpStatus.BAD_REQUEST));
            assertThat(e.getResponseBodyAsString(),containsString("Error"));
        }
    }

    @Test
    public void processNewIntegrationDataSuccessTest(){
        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataType(IntegrationDataType.INCIDENT);
        ResponseEntity<String> response = adapterClient.processNewIntegrationData(integrationData);
        assertThat(response.getStatusCode(),is(HttpStatus.OK));
        assertThat(response.getBody(),containsString("Successful"));
    }
}
