package com.intrasoft.csp.anon.server.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.anon.commons.model.ApplicationId;
import com.intrasoft.csp.anon.server.model.Mapping;
import com.intrasoft.csp.anon.server.model.Rules;
import com.intrasoft.csp.anon.server.repository.MappingRepository;
import com.intrasoft.csp.anon.server.repository.RuleSetRepository;
import com.intrasoft.csp.anon.server.service.RulesService;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RulesServiceImpl implements RulesService {
    private static final Logger LOG = LoggerFactory.getLogger(RulesService.class);

    @Autowired
    RuleSetRepository rulesetRepository;

    @Autowired
    MappingRepository mappingRepository;

    @Override
    public Rules getRule(IntegrationDataType integrationDataType, String cspId) throws IOException {
        Mapping mapping = mappingRepository.findDistinctByDataTypeAndCspId(integrationDataType, cspId.trim());
        Rules rules = null;
        if (mapping != null){
            LOG.debug("Mapping found");
            rules = new ObjectMapper().readerFor(Rules.class).readValue(new String(mapping.getRuleset().getFile()));
        }
        else {
            LOG.debug("Mapping not found");
            mapping = mappingRepository.findDistinctByDataTypeAndCspId(integrationDataType, "**");
            if (mapping != null){
                rules = new ObjectMapper().readerFor(Rules.class).readValue(new String(mapping.getRuleset().getFile()));
            }
        }
        return rules;
    }

    @Override
    public Rules getRule(IntegrationDataType integrationDataType, String cspId, ApplicationId applicationId) throws IOException {
//        Mapping mapping = mappingRepository.findDistinctByDataTypeAndCspIdAndApplicationId(integrationDataType, cspId.trim(), applicationId);
        Mapping mapping = mappingRepository.findTopByDataTypeAndCspIdAndApplicationId(integrationDataType, cspId.trim(), applicationId);
        Rules rules = null;
        if (mapping != null){
            LOG.debug("Mapping found");
            rules = new ObjectMapper().readerFor(Rules.class).readValue(new String(mapping.getRuleset().getFile()));
        }
        else {
            LOG.debug("Mapping not found");
            mapping = mappingRepository.findDistinctByDataTypeAndCspIdAndApplicationId(integrationDataType, "**", applicationId);
            if (mapping != null){
                rules = new ObjectMapper().readerFor(Rules.class).readValue(new String(mapping.getRuleset().getFile()));
            }
        }
        return rules;
    }
}
