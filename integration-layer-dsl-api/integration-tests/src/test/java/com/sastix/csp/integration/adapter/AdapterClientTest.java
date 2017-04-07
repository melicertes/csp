package com.sastix.csp.integration.adapter;

import com.sastix.csp.client.AdapterClient;
import com.sastix.csp.client.config.AdapterClientConfig;
import com.sastix.csp.client.config.AdapterRestTemplateConfiguration;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.IntegrationDataType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by iskitsas on 4/6/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {AdapterClient.class, AdapterRestTemplateConfiguration.class, AdapterClientConfig.class},
        properties = {
                "adapter.server.protocol:http",
                "adapter.server.host:localhost",
                "adapter.server.port:3001"})
public class AdapterClientTest {
    @Autowired
    @Qualifier(value = "adapterClient")
    AdapterClient adapterClient;

    @Test
    public void processNewIntegrationData(){
        IntegrationData integrationDataEmpty = new IntegrationData();
        ResponseEntity<String> response = adapterClient.processNewIntegrationData(integrationDataEmpty);
        assertThat(response.getStatusCode(),is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(),is("Error"));


        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataType(IntegrationDataType.INCIDENT);
        response = adapterClient.processNewIntegrationData(integrationData);
        assertThat(response.getStatusCode(),is(HttpStatus.OK));
        assertThat(response.getBody(),is("Successful"));
    }
}
