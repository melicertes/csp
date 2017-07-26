package com.intrasoft.csp.anon.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.anon.model.Mapping;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.anon.model.Rules;
import com.intrasoft.csp.anon.repository.MappingRepository;
import com.intrasoft.csp.anon.repository.RulesetRepository;
import com.intrasoft.csp.anon.service.RulesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RulesServiceImpl implements RulesService {
    private static final Logger LOG = LoggerFactory.getLogger(RulesService.class);

    @Autowired
    RulesetRepository rulesetRepository;

    @Autowired
    MappingRepository mappingRepository;

    @Override
    public Rules getRule(IntegrationDataType integrationDataType, String cspId) throws IOException {
        Mapping mapping = mappingRepository.findTopByDataTypeAndCspId(integrationDataType, cspId);
        Rules rules = null;
        if (mapping != null){
            rules = new ObjectMapper().readerFor(Rules.class).readValue(new String(mapping.getRuleset().getFile()));
        }
        return rules;
    }
}
