package com.intrasoft.csp.anon.server.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        Mapping mapping = mappingRepository.findTopByDataTypeAndCspId(integrationDataType, cspId.trim());
        Rules rules = null;
        if (mapping != null){
            rules = new ObjectMapper().readerFor(Rules.class).readValue(new String(mapping.getRuleset().getFile()));
        }
        return rules;
    }
}
