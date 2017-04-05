package com.sastix.csp.client;

import com.sastix.csp.commons.model.IntegrationData;
import org.springframework.http.ResponseEntity;

/**
 * Created by iskitsas on 4/4/17.
 */
public interface AdapterClient {
    ResponseEntity<String> processNewIntegrationData(IntegrationData integrationData);
    ResponseEntity<String> updateIntegrationData(IntegrationData integrationData);
    ResponseEntity<String> deleteIntegrationData(IntegrationData integrationData);
}
