package com.intrasoft.csp.conf.integrationtests.sandbox.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.commons.exceptions.InvalidDataTypeException;
import com.intrasoft.csp.conf.client.ConfClient;
import com.intrasoft.csp.conf.client.config.ConfClientConfig;
import com.intrasoft.csp.conf.commons.context.ApiContextUrl;
import com.intrasoft.csp.conf.commons.exceptions.InvalidCspEntryException;
import com.intrasoft.csp.conf.commons.model.AppInfoDTO;
import com.intrasoft.csp.conf.commons.model.RegistrationDTO;
import com.intrasoft.csp.conf.commons.model.UpdateInformationDTO;
import com.intrasoft.csp.conf.commons.types.StatusResponseType;
import com.intrasoft.csp.conf.server.ConfApp;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
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
@Rollback(value = false)
public class ConfClientTest implements ApiContextUrl {

    private static final Logger LOG = LoggerFactory.getLogger(ConfClientTest.class);

    @Autowired
    @Qualifier("ConfRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Autowired
    @Qualifier("confClient")
    ConfClient confClient;


    @Test
    public void updatesInvalidCspTest() throws InvalidDataTypeException, IOException {
        String cspId;

        /**
         * Test a CSP does not exists
         */
        cspId = "12345678-9123-4564-2665-5440000";

        try {
            UpdateInformationDTO updateInformationDTO = confClient.updates(cspId);
            fail("Expected CategoryCannotBeDeletedException");
        }catch (InvalidCspEntryException e){
            Assert.assertThat(e.getMessage(), containsString(StatusResponseType.API_INVALID_CSP_ENTRY.text()));
        }
    }

    @Test
    @Transactional
    public void registerTest() throws IOException {
        String json;
        String cspId;
        RegistrationDTO registration;
        ResponseEntity response;

        /**
         * Test existing CSP with registrationIsUpdate=true
         */
        cspId = "12345678-9123-4564-2665-5440000";
        json = IOUtils.toString(this.getClass().getResourceAsStream("/register/register-valid-1.json"), "UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        registration = new ObjectMapper().readValue(json, RegistrationDTO.class);
        confClient.register(cspId, registration);

    }

    @Test
    public void updateTest() {
        String cspId;
        String updateHash;
        ResponseEntity response;

        /**
         * Test existing CSP with existing hash
         */
        cspId = "12345678-9123-4564-2665-5440000";
        updateHash = "fd61127757973c982cec9d15b61da61a173c8ea86c3122655b92abacec5c7edacff933f896a895d50ecfa3c8f9fc34eb76258bbc228fdf35a5b767a9b1a4c9";
        response = confClient.update(cspId, updateHash);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void appInfoTest() throws IOException {
        String json;
        String cspId;
        AppInfoDTO appInfo;
        ResponseEntity response;

        /**
         * Test existing CSP with registrationIsUpdate=true
         */
        cspId = "12345678-9123-4564-2665-5440000";
        json = IOUtils.toString(this.getClass().getResourceAsStream("/appInfo/appInfo-valid-1.json"), "UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        appInfo = new ObjectMapper().readValue(json, AppInfoDTO.class);
        confClient.appInfo(cspId, appInfo);
    }
}
