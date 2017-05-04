package com.sastix.csp.integration.business.client.csp;

/**
 * Created by iskitsas on 4/28/17.
 */

import com.sastix.csp.client.config.CspRestTemplateConfiguration;
import com.sastix.csp.commons.client.RetryRestTemplate;
import com.sastix.csp.commons.constants.AppProperties;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.IntegrationDataType;
import com.sastix.csp.commons.model.SharingParams;
import com.sastix.csp.commons.routes.ContextUrl;
import com.sastix.csp.integration.MockUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * This test suit is expecting to have a CspApp server up and running with activemq enabled
 * */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {CspRestTemplateConfiguration.class},
        properties = {
                "csp.retry.backOffPeriod:10",//ms
                "csp.retry.maxAttempts:1",
                "server.protocol:http",
                "server.host:localhost",
                "server.port:8081"
})
public class CspAppRequestsTest {

    @Autowired
    @Qualifier("CspRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Autowired
    Environment env;

    String serverUrl;

    @Before
    public void init(){
        String serverProtocol = env.getProperty(AppProperties.SERVER_PROTOCOL);
        String serverHost = env.getProperty(AppProperties.SERVER_HOST);
        String serverPort = env.getProperty(AppProperties.SERVER_PORT);
        serverUrl = serverProtocol+"://"+serverHost+":"+serverPort;
    }

    @Test
    public void makePostRequestWithDataTest(){

        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataType(IntegrationDataType.INCIDENT);
        SharingParams sharingParams = new SharingParams();
        sharingParams.setIsExternal(false);
        sharingParams.setToShare(true);
        integrationData.setSharingParams(sharingParams);

        String url = serverUrl + ContextUrl.DSL_INTEGRATION_DATA;
        ResponseEntity<String> response = retryRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<Object>(integrationData), String.class);

        assertThat(response.getStatusCode(),is(HttpStatus.OK));
    }
}
