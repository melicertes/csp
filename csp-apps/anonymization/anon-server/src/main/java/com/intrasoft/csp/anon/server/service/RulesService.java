package com.intrasoft.csp.anon.server.service;

import com.intrasoft.csp.anon.commons.model.ApplicationId;
import com.intrasoft.csp.anon.server.model.Rules;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by chris on 18/7/2017.
 */
@Service
public interface RulesService {

    public Rules getRule(IntegrationDataType integrationDataType, String cspId) throws IOException;
    public Rules getRule(IntegrationDataType integrationDataType, String cspId, ApplicationId applicationId) throws IOException;

}
