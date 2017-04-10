package com.sastix.csp.integration.sandbox.client.adapter;

import com.sastix.csp.client.AdapterClient;
import com.sastix.csp.client.config.AdapterClientConfig;
import com.sastix.csp.client.config.CspRestTemplateConfiguration;
import com.sastix.csp.commons.client.RetryRestTemplate;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.IntegrationDataType;
import com.sastix.csp.commons.model.SharingParams;
import com.sastix.csp.commons.routes.ContextUrl;
import com.sastix.csp.integration.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Created by iskitsas on 4/4/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {AdapterClient.class, CspRestTemplateConfiguration.class, AdapterClientConfig.class}) //SB 1.5
public class AdapterClientSandboxTest {
    @Mock
    AdapterClient adapterMockClient;

    @Autowired
    @Qualifier("CspRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Autowired
    AdapterClient adapterClient;

    private String adapterContext;

    @Before
    public void init() throws IOException, URISyntaxException {
        MockitoAnnotations.initMocks(this);
        IntegrationData integrationDataEmpty = new IntegrationData();
        Mockito.when(adapterMockClient.processNewIntegrationData(integrationDataEmpty)).thenReturn(new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST));

        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataType(IntegrationDataType.INCIDENT);
        Mockito.when(adapterMockClient.processNewIntegrationData(integrationData)).thenReturn(new ResponseEntity<>("Successful", HttpStatus.OK));

        adapterContext = adapterClient.getContext();
    }

    /**
     * Not of real value, just to demonstrate Mockito and MockServer
     * */
    @Test
    public void processNewIntegrationData(){
        IntegrationData integrationDataEmpty = new IntegrationData();
        ResponseEntity<String> response = adapterMockClient.processNewIntegrationData(integrationDataEmpty);
        assertThat(response.getStatusCode(),is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(),is("Error"));


        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataType(IntegrationDataType.INCIDENT);
        response = adapterMockClient.processNewIntegrationData(integrationData);
        assertThat(response.getStatusCode(),is(HttpStatus.OK));
        assertThat(response.getBody(),is("Successful"));
    }

    @Test
    public void processDataTest() throws IOException {
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(retryRestTemplate).build();
        mockServer.expect(requestTo(adapterContext+ ContextUrl.ADAPTER_INTEGRATION_DATA))
                .andRespond(withSuccess(TestUtil.convertObjectToJsonBytes(new ResponseEntity<>("Successful", HttpStatus.OK)),TestUtil.APPLICATION_JSON_UTF8));


        IntegrationData data = new IntegrationData();
        data.setDataType(IntegrationDataType.INCIDENT);
        SharingParams sharingParams = new SharingParams();
        sharingParams.setIsExternal(false);
        sharingParams.setToShare(true);
        data.setSharingParams(sharingParams);
        ResponseEntity<String> response = adapterClient.processNewIntegrationData(data);
        assertThat(response.getStatusCode(),is(HttpStatus.OK));
        assertThat(response.getBody(),containsString("Successful"));

        mockServer.verify();
    }
}
