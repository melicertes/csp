package com.intrasoft.csp.integration.sandbox.client.anon;

import com.intrasoft.csp.anon.AnonApp;
import com.intrasoft.csp.client.AnonClient;
import com.intrasoft.csp.client.config.AnonClientConfig;
import com.intrasoft.csp.client.config.CspRestTemplateConfiguration;
import com.intrasoft.csp.client.impl.AnonClientImpl;
import com.intrasoft.csp.commons.client.ApiVersionClient;
import com.intrasoft.csp.commons.client.RetryRestTemplate;
import com.intrasoft.csp.commons.model.DataParams;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.SharingParams;
import com.intrasoft.csp.commons.routes.ContextUrl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AnonApp.class, AnonClientConfig.class, CspRestTemplateConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "server.port: 8585",
                "server.protocol: http",
                "server.host: localhost",
                "api.version: 1",
                "csp.retry.backOffPeriod:10",
                "csp.retry.maxAttempts:1",
                //these are already defined in application.properties OR application-mysql.properties
//                "spring.datasource.url=jdbc:postgresql://localhost:5432/anonymization",
//                "spring.datasource.username=anon",
//                "spring.datasource.password=@n0nu$er",
                "key.update=10000"
        })
public class AnonClientTest {
    private static final Logger LOG = LoggerFactory.getLogger(AnonClientTest.class);

    @Autowired
    RetryRestTemplate retryRestTemplate;

    @Autowired
    @Qualifier("anonClient")
    AnonClient anonClient;

    @Test
    public void sendPostIntegrationDataTest() throws IOException {
        String apiUrl = "v1/anon";

        /*IntegrationData integrationData = new IntegrationData();
        integrationData.setDataType(IntegrationDataType.TRUSTCIRCLE);
        DataParams dataParams = new DataParams();
        dataParams.setCspId("9");
        integrationData.setDataParams(dataParams);
        SharingParams sharingParams = new SharingParams();
        sharingParams.setIsExternal(false);
        sharingParams.setToShare(true);
        integrationData.setSharingParams(sharingParams);
        ResponseEntity<String> response = anonClient.postAnonData(integrationData, DATA_ANONYMIZATION);
        assertThat(response.getStatusCode(),is(HttpStatus.OK));*/
    }
}


