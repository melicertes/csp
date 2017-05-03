package com.sastix.csp.client;

import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.VersionDTO;
import org.springframework.http.ResponseEntity;

/**
 * Created by iskitsas on 5/3/17.
 */
public interface CspClient {
    VersionDTO getApiVersion();
    ResponseEntity<String> postIntegrationData(IntegrationData integrationData, String context);
    ResponseEntity<String> updateIntegrationData(IntegrationData integrationData, String context);
    ResponseEntity<String> deleteIntegrationData(IntegrationData integrationData, String context);
}
