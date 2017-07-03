package com.intrasoft.csp.client;

import com.intrasoft.csp.commons.model.IntegrationData;
import org.springframework.http.ResponseEntity;

/**
 * Created by iskitsas on 4/4/17.
 */
public interface AdapterClient {
    ResponseEntity<String> processNewIntegrationData(IntegrationData integrationData);
    ResponseEntity<String> updateIntegrationData(IntegrationData integrationData);
    ResponseEntity<String> deleteIntegrationData(IntegrationData integrationData);
    void setProtocolHostPort(String protocol, String host, String port);
    String getContext();
}
