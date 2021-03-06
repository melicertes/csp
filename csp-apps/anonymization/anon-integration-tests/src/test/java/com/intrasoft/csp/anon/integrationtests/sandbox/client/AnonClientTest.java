package com.intrasoft.csp.anon.integrationtests.sandbox.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.anon.client.AnonClient;
import com.intrasoft.csp.anon.client.config.AnonClientConfig;

import com.intrasoft.csp.anon.commons.exceptions.AnonException;
import com.intrasoft.csp.anon.commons.exceptions.MappingNotFoundForGivenTupleException;
import com.intrasoft.csp.anon.commons.model.AnonContextUrl;
import com.intrasoft.csp.anon.commons.model.IntegrationAnonData;

import com.intrasoft.csp.anon.server.AnonApp;
import com.intrasoft.csp.commons.apiHttpStatusResponse.HttpStatusResponseType;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AnonApp.class, AnonClientConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "spring.datasource.url:jdbc:h2:mem:anon",
                "server.port: 8585",
                "anon.server.protocol: http",
                "anon.server.host: localhost",
                "anon.server.port: 8585",
                "api.version: 1",
                "csp.retry.backOffPeriod:10",
                "csp.retry.maxAttempts:1",
                "key.update=10000",
                "enable.oam:false"
        })
public class AnonClientTest implements AnonContextUrl {
    private static final Logger LOG = LoggerFactory.getLogger(AnonClientTest.class);

    @Autowired
    @Qualifier("AnonRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Autowired
    @Qualifier("anonClient")
    AnonClient anonClient;

    @Test
    public void sendPostIntegrationDataTest() throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        IntegrationAnonData data = new IntegrationAnonData();
        data.setDataType(IntegrationDataType.TRUSTCIRCLE);
        data.setCspId("9");

        try {
            anonClient.postAnonData(data);
            fail("Expected MappingNotFoundForGivenTupleException");
        } catch (MappingNotFoundForGivenTupleException e) {
            Assert.assertThat(e.getMessage(), containsString(HttpStatusResponseType.MAPPING_NOT_FOUND_FOR_GIVEN_TUPLE.getReasonPhrase()));
        }
    }
}

