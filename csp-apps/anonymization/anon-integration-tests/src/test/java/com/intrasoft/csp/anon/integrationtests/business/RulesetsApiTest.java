package com.intrasoft.csp.anon.integrationtests.business;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.anon.client.AnonClient;
import com.intrasoft.csp.anon.client.config.AnonClientConfig;
import com.intrasoft.csp.anon.commons.model.AnonContextUrl;
import com.intrasoft.csp.anon.commons.model.RuleSetDTO;
import com.intrasoft.csp.anon.server.AnonApp;
import com.intrasoft.csp.anon.server.model.Rule;
import com.intrasoft.csp.anon.server.model.Rules;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
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

import java.util.ArrayList;
import java.util.List;

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
                "key.update=10000",
                "enable.oam:false"
        })
@ActiveProfiles("h2mem")
public class RulesetsApiTest implements AnonContextUrl {

    private static final Logger LOG = LoggerFactory.getLogger(RulesetsApiTest.class);

    @Autowired
    @Qualifier("AnonRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Autowired
    @Qualifier("anonClient")
    AnonClient anonClient;

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    public void saveRuleset() throws JsonProcessingException {

        // Given
        RuleSetDTO ruleSetDTO = createRuleset();

        // When
        anonClient.saveRuleSet(ruleSetDTO);

        //Then
        assertThat(anonClient.getAllRuleSet().size(), equalTo(1));
        LOG.info(anonClient.getAllRuleSet().toString());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    public void getRuleset() throws JsonProcessingException {

        // Given
        RuleSetDTO ruleSetDTO = createRuleset();
        ruleSetDTO = anonClient.saveRuleSet(ruleSetDTO);

        // When
        RuleSetDTO fetchedRuleSetDTO = anonClient.getRuleSetById(ruleSetDTO.getId());

        //Then
        assertThat(fetchedRuleSetDTO.getFilename(), equalTo("TrustCircleRulesFile"));
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    public void deleteRuleset() throws JsonProcessingException {

        // Given
        RuleSetDTO ruleSetDTO = createRuleset();
        ruleSetDTO = anonClient.saveRuleSet(ruleSetDTO);

        // When
        anonClient.deleteRuleSet(ruleSetDTO.getId());

        //Then
        assertThat(anonClient.getAllRuleSet().size(), equalTo(0));
    }

}
