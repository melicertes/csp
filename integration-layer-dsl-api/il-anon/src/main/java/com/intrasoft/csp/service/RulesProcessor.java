package com.intrasoft.csp.service;

import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.model.Rules;
import org.springframework.stereotype.Service;

/**
 * Created by chris on 18/7/2017.
 */
@Service
public interface RulesProcessor {

    public Rules getRule(IntegrationDataType integrationDataType, String cspId);
}
