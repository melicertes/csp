package com.intrasoft.csp.anon.integrationtests.business;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.intrasoft.csp.anon.client.AnonClient;
import com.intrasoft.csp.anon.client.config.AnonClientConfig;
import com.intrasoft.csp.anon.commons.model.MappingDTO;
import com.intrasoft.csp.anon.commons.model.RuleSetDTO;
import com.intrasoft.csp.anon.integrationtests.business.utils.Helper;
import com.intrasoft.csp.anon.server.AnonApp;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static com.intrasoft.csp.anon.integrationtests.business.utils.Helper.createRuleset;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

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
public class MappingsApiTest {

    private static final Logger LOG = LoggerFactory.getLogger(MappingsApiTest.class);

    @Autowired
    @Qualifier("AnonRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Autowired
    @Qualifier("anonClient")
    AnonClient anonClient;

    private RuleSetDTO ruleSetDTO;

    @Before
    public void populateH2Db() throws JsonProcessingException {
        ruleSetDTO = Helper.createRuleset();
        ruleSetDTO = anonClient.saveRuleSet(ruleSetDTO);

    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    public void saveMapping() throws JsonProcessingException {

        // Given
        MappingDTO mappingDTO = Helper.createMapping(ruleSetDTO);


        // When
        anonClient.saveMapping(mappingDTO);

        //Then
        assertThat(anonClient.getAllMappings().size(), equalTo(1));
        LOG.info(anonClient.getAllMappings().toString());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    public void getMapping() throws JsonProcessingException {

        // Given
        MappingDTO mappingDTO = Helper.createMapping(ruleSetDTO);
        mappingDTO = anonClient.saveMapping(mappingDTO);

        // When
        MappingDTO fetchedMapping = anonClient.getMappingById(mappingDTO.getId());

        //Then
        assertThat(fetchedMapping.getCspId(), equalTo("CERT-BUND"));
        LOG.info(fetchedMapping.toString());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    public void deleteMapping() throws JsonProcessingException {

        // Given
        MappingDTO mappingDTO = Helper.createMapping(ruleSetDTO);
        mappingDTO = anonClient.saveMapping(mappingDTO);

        // When
        anonClient.deleteMapping(mappingDTO.getId());

        //Then
        assertThat(anonClient.getAllMappings().size(), equalTo(0));
    }
}
