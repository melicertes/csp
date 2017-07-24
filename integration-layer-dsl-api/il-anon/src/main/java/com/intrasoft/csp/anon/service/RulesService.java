package com.intrasoft.csp.anon.service;

import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.anon.model.Rules;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by chris on 18/7/2017.
 */
@Service
public interface RulesService {

    public Rules getRule(IntegrationDataType integrationDataType, String cspId) throws IOException;

}
