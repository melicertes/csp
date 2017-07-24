package com.intrasoft.csp.service;

import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.model.Rules;
import com.intrasoft.csp.model.Ruleset;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by chris on 18/7/2017.
 */
@Service
public interface RulesProcessor {

    public Rules getRule(IntegrationDataType integrationDataType, String cspId) throws IOException;

}
