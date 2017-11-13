package com.intrasoft.csp.misp.client;

import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.misp.commons.config.ApiContextUrl;
import com.intrasoft.csp.misp.commons.config.MispContextUrl;
import org.springframework.http.ResponseEntity;

public interface MispClient {

    IntegrationData postIntegrationDataAdapter(IntegrationData integrationData);
    IntegrationData postIntegrationDataEmitter(IntegrationData integrationData);

}
