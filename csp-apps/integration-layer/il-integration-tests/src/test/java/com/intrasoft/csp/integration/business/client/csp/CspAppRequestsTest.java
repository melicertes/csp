package com.intrasoft.csp.integration.business.client.csp;

/**
 * Created by iskitsas on 4/28/17.
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.intrasoft.csp.client.config.CspClientConfig;
import com.intrasoft.csp.commons.constants.AppProperties;
import com.intrasoft.csp.commons.model.DataParams;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.SharingParams;
import com.intrasoft.csp.commons.routes.ContextUrl;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import org.apache.commons.io.FileUtils;
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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * This test suit is expecting to have a CspApp server up and running with activemq enabled
 * */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {CspClientConfig.class},
        properties = {
                "csp.retry.backOffPeriod:10",//ms
                "csp.retry.maxAttempts:1",
                "server.protocol:http",
                "server.host:localhost",
                "server.port:8080"
})
public class CspAppRequestsTest {

    @Autowired
    @Qualifier("CspRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Autowired
    Environment env;

    String serverUrl;

    URL integration_data_vulnerability = getClass().getClassLoader().getResource("json/integration_data_vulnerability.json");

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

        String url = serverUrl +"/v"+ContextUrl.REST_API_V1+ ContextUrl.DSL_INTEGRATION_DATA;
        ResponseEntity<String> response = retryRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<Object>(integrationData), String.class);

        assertThat(response.getStatusCode(),is(HttpStatus.OK));
    }

    @Test
    public void shouldNotPropagateILInternalErrorTest() throws URISyntaxException, IOException, InterruptedException {
        String json = FileUtils.readFileToString(new File(integration_data_vulnerability.toURI()), Charset.forName("UTF-8"));
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JodaModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        IntegrationData integrationData = mapper.readValue(json, IntegrationData.class);

        String url = serverUrl +"/v"+ContextUrl.REST_API_V1+ ContextUrl.DSL_INTEGRATION_DATA;
        ResponseEntity<String> response = retryRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<Object>(integrationData), String.class);

        assertThat(response.getStatusCode(),is(HttpStatus.OK));

        response = retryRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<Object>(integrationData), String.class);

        assertThat(response.getStatusCode(),is(HttpStatus.OK));
    }
}
