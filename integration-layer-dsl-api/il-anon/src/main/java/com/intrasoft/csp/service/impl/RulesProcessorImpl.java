package com.intrasoft.csp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.model.Ruleset;
import com.intrasoft.csp.model.IntegrationAnonData;
import com.intrasoft.csp.model.Rules;
import com.intrasoft.csp.repository.IntegrationAnonDataRepository;
import com.intrasoft.csp.repository.RulesetRepository;
import com.intrasoft.csp.service.RulesProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RulesProcessorImpl implements RulesProcessor{
    private static final Logger LOG = LoggerFactory.getLogger(RulesProcessor.class);

    @Autowired
    RulesetRepository rulesetRepository;

    @Autowired
    IntegrationAnonDataRepository integrationAnonDataRepository;

    @Override
    public Rules getRule(IntegrationDataType integrationDataType, String cspId) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        IntegrationAnonData integrationAnonData = integrationAnonDataRepository.findDistinctByDataType(integrationDataType).get(0);
        Rules rules = new ObjectMapper().readerFor(Rules.class).readValue(new String(integrationAnonData.getRuleset().getFile()));
        return rules;
    }
}
