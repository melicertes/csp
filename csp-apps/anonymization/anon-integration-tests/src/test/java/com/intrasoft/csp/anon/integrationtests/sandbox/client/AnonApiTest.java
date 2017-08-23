package com.intrasoft.csp.anon.integrationtests.sandbox.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.anon.client.AnonClient;
import com.intrasoft.csp.anon.client.config.AnonClientConfig;
import com.intrasoft.csp.anon.commons.exceptions.MappingNotFoundForGivenTupleException;
import com.intrasoft.csp.anon.commons.model.AnonContextUrl;
import com.intrasoft.csp.anon.commons.model.IntegrationAnonData;
import com.intrasoft.csp.anon.server.AnonApp;
import com.intrasoft.csp.commons.apiHttpStatusResponse.HttpStatusResponseType;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.hamcrest.Matchers.containsString;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AnonApp.class, AnonClientConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "server.port: 8585",
                "anon.server.protocol: http",
                "anon.server.host: localhost",
                "anon.server.port: 8585",
                "api.version: 1",
                "csp.retry.backOffPeriod:10",
                "csp.retry.maxAttempts:1",
                "key.update=10000"
        })
@ActiveProfiles("h2mem") //TODO: to be changed to use H2 DB profile
public class AnonApiTest implements AnonContextUrl {

    private static final Logger LOG = LoggerFactory.getLogger(AnonApiTest.class);

    @Autowired
    @Qualifier("AnonRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Autowired
    @Qualifier("anonClient")
    AnonClient anonClient;

    @Test
    public void anonymizeTrustCircleTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = IOUtils.toString(this.getClass().getResourceAsStream("/trustcircle.json"), "UTF-8");
        IntegrationAnonData integrationAnonData = mapper.readValue(json, IntegrationAnonData.class);
        try {
            ResponseEntity<String> response = anonClient.postAnonData(integrationAnonData);
            Assert.assertThat(response.getBody(), containsString("\"short_name\":\"*******\""));
            Assert.assertThat(response.getBody(), containsString("\"description\":\"*******\""));
        } catch (MappingNotFoundForGivenTupleException e) {
            Assert.fail(HttpStatusResponseType.MAPPING_NOT_FOUND_FOR_GIVEN_TUPLE.getReasonPhrase());
        }
    }

    @Test
    public void mappingNotFoundTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = IOUtils.toString(this.getClass().getResourceAsStream("/threat.json"), "UTF-8");
        IntegrationAnonData integrationAnonData = mapper.readValue(json, IntegrationAnonData.class);
        try {
            ResponseEntity<String> response = anonClient.postAnonData(integrationAnonData);
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), containsString(HttpStatusResponseType.MAPPING_NOT_FOUND_FOR_GIVEN_TUPLE.getReasonPhrase()));
        }
    }

    @Test
    public void unsupportedDataTypeTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = IOUtils.toString(this.getClass().getResourceAsStream("/invalidDataType.json"), "UTF-8");
        IntegrationAnonData integrationAnonData = mapper.readValue(json, IntegrationAnonData.class);
        try {
            ResponseEntity<String> response = anonClient.postAnonData(integrationAnonData);
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), containsString(HttpStatusResponseType.UNSUPPORTED_DATA_TYPE.getReasonPhrase()));
        }
    }

    @Test
    public void malformedDataStructureTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = IOUtils.toString(this.getClass().getResourceAsStream("/malformed.json"), "UTF-8");
        IntegrationAnonData integrationAnonData = mapper.readValue(json, IntegrationAnonData.class);
        try {
            ResponseEntity<String> response = anonClient.postAnonData(integrationAnonData);
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), containsString(HttpStatusResponseType.MALFORMED_INTEGRATION_DATA_STRUCTURE.getReasonPhrase()));
        }
    }

}
