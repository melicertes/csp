package com.intrasoft.csp.conf.integrationtests.sandbox.client;

import com.intrasoft.csp.conf.client.ConfClient;
import com.intrasoft.csp.conf.client.config.ConfClientConfig;
import com.intrasoft.csp.conf.commons.context.ApiContextUrl;
import com.intrasoft.csp.conf.commons.exceptions.InvalidCspEntryException;
import com.intrasoft.csp.conf.commons.exceptions.UpdateInvalidHashEntryException;
import com.intrasoft.csp.conf.commons.exceptions.UpdateNotFoundException;
import com.intrasoft.csp.conf.commons.types.StatusResponseType;
import com.intrasoft.csp.conf.server.ConfApp;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ConfApp.class, ConfClientConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "conf.server.port:8090",
                "conf.server.protocol:http",
                "conf.server.host:localhost",

                "conf.client.ssl.enabled:false",

                "conf.retry.backOffPeriod:5000",
                "conf.retry.maxAttempts:0"
        })
@ActiveProfiles("tests")
public class ConfClientUpdateTests implements ApiContextUrl {

    private static final Logger LOG = LoggerFactory.getLogger(ConfClientTest.class);

    @Autowired
    @Qualifier("ConfRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Autowired
    @Qualifier("confClient")
    ConfClient confClient;


    /**
     * Test for a CSP that does not exist
     */
    @Test
    public void invalidCspUpdateTest() {
        String cspId = "invalid";
        String hash = "x";

        try {
            ResponseEntity responseEntity = confClient.update(cspId, hash);
            fail("Expected CategoryCannotBeDeletedException");
        }catch (InvalidCspEntryException e){
            Assert.assertThat(e.getMessage(), containsString(StatusResponseType.API_INVALID_CSP_ENTRY.text()));
        }
    }

    /**
     * Test for a valid CSP with invalid update hash
     */
    @Test
    public void invalidHashUpdateTest() {
        String cspId = "11111111-1111-1111-1111-111111111111";
        String hash = "x";

        try {
            ResponseEntity responseEntity = confClient.update(cspId, hash);
            fail("Expected CategoryCannotBeDeletedException");
        }catch (UpdateInvalidHashEntryException e){
            Assert.assertThat(e.getMessage(), containsString(StatusResponseType.API_UPDATE_INVALID_HASH_ENTRY.text()));
        }
    }

    /**
     * Test for a valid CSP with valid hash, where actual file does not exist within the local repository
     */
    @Test
    public void updateNotFoundUpdateTest() {
        String cspId = "33333333-3333-3333-3333-333333333333";
        String hash = "cdd9f8683877d46e7a98c591433d2792a76919d1041ccf9d5da66ceb40aa97ac6b48af40cd39b5544c8724951f570223dd1a90a5d8abfd87467ad3c7d833";

        try {
            ResponseEntity responseEntity = confClient.update(cspId, hash);
            fail("Expected CategoryCannotBeDeletedException");
        }catch (UpdateNotFoundException e){
            Assert.assertThat(e.getMessage(), containsString(StatusResponseType.API_UPDATE_NOT_FOUND.text()));
        }
    }

    /**
     * Test for a valid CSP, with valid hash, and existing update file
     */
    @Test
    public void validUpdateTest() throws IOException {
        String cspId = "11111111-1111-1111-1111-111111111111";
        String hash = "fd61127757973c982cec9d15b61da61a173c8ea86c3122655b92abacec5c7edacff933f896a895d50ecfa3c8f9fc34eb76258bbc228fdf35a5b767a9b1a4c9";

        ResponseEntity<Resource> responseEntity = confClient.update(cspId, hash);
        InputStream responseInputStream;


        responseInputStream = responseEntity.getBody().getInputStream();
        //response size is more than 100-bytes
        Assert.assertThat(responseInputStream.available(), greaterThan(100));
        //status code is OK
        Assert.assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.OK));
        //header contains original update file name
        Assert.assertThat(responseEntity.getHeaders().get("Content-Disposition").get(0).toString(), containsString(hash));

    }
}
