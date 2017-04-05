package com.sastix.csp.integration.adapter;

import com.sastix.csp.client.AdapterClient;
import com.sastix.csp.client.config.AdapterRestTemplateConfiguration;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.IntegrationDataType;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.web.WebClientAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by iskitsas on 4/4/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = {AdapterClient.class, AdapterRestTemplateConfiguration.class}) //SB 1.5
@SpringBootTest(classes = {AdapterClient.class, AdapterRestTemplateConfiguration.class})
public class AdapterClientTest {
    @Mock
    AdapterClient adapterClient;

    @Before
    public void init() throws IOException, URISyntaxException {
        MockitoAnnotations.initMocks(this);
        IntegrationData integrationDataEmpty = new IntegrationData();
        Mockito.when(adapterClient.processNewIntegrationData(integrationDataEmpty)).thenReturn(new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST));

        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataType(IntegrationDataType.INCIDENT);
        Mockito.when(adapterClient.processNewIntegrationData(integrationData)).thenReturn(new ResponseEntity<>("Successful", HttpStatus.OK));
    }

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
