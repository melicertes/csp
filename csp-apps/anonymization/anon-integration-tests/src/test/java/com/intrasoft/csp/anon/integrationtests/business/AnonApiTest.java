package com.intrasoft.csp.anon.integrationtests.business;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.anon.client.AnonClient;
import com.intrasoft.csp.anon.client.config.AnonClientConfig;
import com.intrasoft.csp.anon.commons.exceptions.MappingNotFoundForGivenTupleException;
import com.intrasoft.csp.anon.commons.model.AnonContextUrl;
import com.intrasoft.csp.anon.commons.model.IntegrationAnonData;
import com.intrasoft.csp.anon.commons.model.MappingDTO;
import com.intrasoft.csp.anon.commons.model.RuleSetDTO;
import com.intrasoft.csp.anon.integrationtests.business.utils.Helper;
import com.intrasoft.csp.anon.server.AnonApp;
import com.intrasoft.csp.anon.server.model.Mapping;
import com.intrasoft.csp.anon.server.model.RuleSet;
import com.intrasoft.csp.commons.apiHttpStatusResponse.HttpStatusResponseType;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
@ActiveProfiles("h2mem")
public class AnonApiTest implements AnonContextUrl {

    private static final Logger LOG = LoggerFactory.getLogger(AnonApiTest.class);

    @Autowired
    @Qualifier("AnonRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Autowired
    @Qualifier("anonClient")
    AnonClient anonClient;

    @Autowired
    ObjectMapper objectMapper;

    @Before
    public void populateH2Db() throws JsonProcessingException {
        RuleSetDTO ruleSetDTO = Helper.createRuleset();
        anonClient.saveRuleSet(ruleSetDTO);

        ruleSetDTO.setId(new Long(1));
        MappingDTO mappingDTO = Helper.createMapping(ruleSetDTO);
        anonClient.saveMapping(mappingDTO);
    }

    @Test
    public void anonymizeTrustCircleTest() throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        ObjectMapper mapper = new ObjectMapper();
        String json = IOUtils.toString(this.getClass().getResourceAsStream("/trustcircle.json"), "UTF-8");
        IntegrationAnonData integrationAnonData = mapper.readValue(json, IntegrationAnonData.class);
        try {
            IntegrationAnonData anonData = anonClient.postAnonData(integrationAnonData);
            String response = objectMapper.writeValueAsString(anonData.getDataObject());
            Assert.assertThat(response, containsString("\"short_name\":\"*******\""));
            Assert.assertThat(response, containsString("\"created\":\"##########\""));
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
            anonClient.postAnonData(integrationAnonData);
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
            anonClient.postAnonData(integrationAnonData);
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
            anonClient.postAnonData(integrationAnonData);
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), containsString(HttpStatusResponseType.MALFORMED_INTEGRATION_DATA_STRUCTURE.getReasonPhrase()));
        }
    }

}
