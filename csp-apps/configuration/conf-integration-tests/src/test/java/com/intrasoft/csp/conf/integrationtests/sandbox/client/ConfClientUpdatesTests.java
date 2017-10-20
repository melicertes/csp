package com.intrasoft.csp.conf.integrationtests.sandbox.client;

import com.intrasoft.csp.conf.client.ConfClient;
import com.intrasoft.csp.conf.client.config.ConfClientConfig;
import com.intrasoft.csp.conf.commons.context.ApiContextUrl;
import com.intrasoft.csp.conf.commons.exceptions.InvalidCspEntryException;
import com.intrasoft.csp.conf.commons.model.api.UpdateInformationDTO;
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

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

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
public class ConfClientUpdatesTests implements ApiContextUrl {

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
    public void invalidCspUpdatesTest() {
        String cspId = "invalid";

        try {
            UpdateInformationDTO updateInformationDTO = confClient.updates(cspId);
            fail("Expected CategoryCannotBeDeletedException");
        }catch (InvalidCspEntryException e){
            Assert.assertThat(e.getMessage(), containsString(StatusResponseType.API_INVALID_CSP_ENTRY.text()));
        }
    }

    /**
     * Test for a valid CSP, that has updates
     */
    @Test
    public void validCspHasUpdatesTest() {
        String cspId = "11111111-1111-1111-1111-111111111111";

        UpdateInformationDTO updateInformationDTO = confClient.updates(cspId);
        Assert.assertThat(updateInformationDTO.getAvailable().size(), equalTo(2));
        Assert.assertThat(updateInformationDTO.getAvailable().get("module1").size(), equalTo(0));
        Assert.assertThat(updateInformationDTO.getAvailable().get("module2").get(0).getVersion(), equalTo("1.0.000"));
    }

    /**
     * Test for a valid CSP, that is not yet configured via UI to receive updates
     */
    @Test
    public void cspNotConfiguredForUpdatesTest() {
        String cspId = "22222222-2222-2222-2222-222222222222";

        try {
            UpdateInformationDTO updateInformationDTO = confClient.updates(cspId);
            fail("Expected CategoryCannotBeDeletedException");
        }catch (InvalidCspEntryException e){
            Assert.assertThat(e.getMessage(), containsString(StatusResponseType.API_CSP_NOT_CONFIGURED_YET.text()));
        }
    }





}
