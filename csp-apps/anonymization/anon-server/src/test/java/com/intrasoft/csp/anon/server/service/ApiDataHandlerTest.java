package com.intrasoft.csp.anon.server.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.anon.commons.model.IntegrationAnonData;
import com.intrasoft.csp.anon.commons.model.MappingDTO;
import com.intrasoft.csp.anon.commons.model.RuleSetDTO;
import com.intrasoft.csp.anon.commons.model.SaveMappingDTO;
import com.intrasoft.csp.anon.server.AnonApp;
import com.intrasoft.csp.anon.server.model.Rules;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AnonApp.class},
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
                "key.update=10000"
        })
public class ApiDataHandlerTest {
    @Autowired
    ApiDataHandler apiDataHandler;

    URL data_incident = getClass().getClassLoader().getResource("data_incident.json");
    URL data_incident2 = getClass().getClassLoader().getResource("data_incident2.json");
    URL incident_rules_email_ip_string = getClass().getClassLoader().getResource("incident_rules_email_ip_string.json");

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AnonService anonService;

    @Autowired
    RulesService rulesService;

    @DirtiesContext
    @Test
    public void handleAnonIntegrationDataTest() throws URISyntaxException, IOException, InvalidKeyException, NoSuchAlgorithmException {
        String json = FileUtils.readFileToString(new File(data_incident.toURI()), Charset.forName("UTF-8"));
        IntegrationData integrationData = objectMapper.readValue(json,IntegrationData.class);

        String cspId = integrationData.getDataParams().getCspId();
        //insert mapping and ruleset
        RuleSetDTO ruleSetDTO = new RuleSetDTO();
        ruleSetDTO.setFilename(new File(incident_rules_email_ip_string.getFile()).getName());
        ruleSetDTO.setFile(FileUtils.readFileToByteArray(new File(incident_rules_email_ip_string.toURI())));
        ruleSetDTO.setDescription("incident ruleset");
        RuleSetDTO savedRuleSet = anonService.saveRuleSet(ruleSetDTO);

        MappingDTO mappingDTO = new MappingDTO(cspId,savedRuleSet,integrationData.getDataType());
        MappingDTO savedMapping = anonService.saveMapping(mappingDTO);

        IntegrationAnonData integrationAnonData = new IntegrationAnonData();
        integrationAnonData.setCspId(cspId);
        integrationAnonData.setDataType(integrationData.getDataType());
        integrationAnonData.setDataObject(integrationData.getDataObject());

        IntegrationAnonData anonData = apiDataHandler.handleAnonIntegrationData(integrationAnonData);

        String jsonOut = objectMapper.writeValueAsString(anonData.getDataObject());
        assertThat(jsonOut, containsString("\"classification.taxonomy\":\""));//cannot really test the randomness of anon here
        assertThat(jsonOut, containsString("\"classification.identifier\":\""));//cannot really test the randomness of anon here
        assertThat(jsonOut, containsString("\"destination.local_hostname\":\"hostname\""));
        assertThat(jsonOut, containsString("\"destination.local_ip\":\"***.***.***.***\""));
        assertThat(jsonOut, containsString("\"destination.account\":\"***@******.**\""));
        assertThat(jsonOut, containsString("\"destination.asn\":\"##########\""));
    }

    @DirtiesContext
    @Test
    public void rulesServiceTest() throws IOException, URISyntaxException {
        String json = FileUtils.readFileToString(new File(data_incident.toURI()), Charset.forName("UTF-8"));
        String json2 = FileUtils.readFileToString(new File(data_incident2.toURI()), Charset.forName("UTF-8"));
        IntegrationData integrationData = objectMapper.readValue(json,IntegrationData.class);
        IntegrationData integrationData2 = objectMapper.readValue(json2,IntegrationData.class);

        String cspId = integrationData.getDataParams().getCspId();
        //insert mapping and ruleset
        RuleSetDTO ruleSetDTO = new RuleSetDTO();
        ruleSetDTO.setFilename(new File(incident_rules_email_ip_string.getFile()).getName());
        ruleSetDTO.setFile(FileUtils.readFileToByteArray(new File(incident_rules_email_ip_string.toURI())));
        ruleSetDTO.setDescription("incident ruleset");
        RuleSetDTO savedRuleSet = anonService.saveRuleSet(ruleSetDTO);

        MappingDTO mappingDTO = new MappingDTO(cspId,savedRuleSet,integrationData.getDataType());
        MappingDTO savedMapping = anonService.saveMapping(mappingDTO);

        mappingDTO = new MappingDTO(cspId,savedRuleSet,integrationData2.getDataType());
        MappingDTO savedMapping2 = anonService.saveMapping(mappingDTO);

        Rules rules = rulesService.getRule(IntegrationDataType.INCIDENT,"demo1-csp ");//test if the value is trimmed

        assertThat(rules.getRules().size(),greaterThan(0));
    }
}
