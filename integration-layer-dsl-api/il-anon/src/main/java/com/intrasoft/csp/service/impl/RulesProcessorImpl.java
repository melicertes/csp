package com.intrasoft.csp.service.impl;

import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.model.Rules;
import com.intrasoft.csp.service.RulesProcessor;
import org.springframework.stereotype.Component;

@Component
public class RulesProcessorImpl implements RulesProcessor{
    @Override
    public Rules getRule(IntegrationDataType integrationDataType, String cspId) {
        return null;
    }
}
