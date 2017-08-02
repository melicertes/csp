package com.intrasoft.csp.client;

import com.intrasoft.csp.commons.exceptions.InvalidDataTypeException;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.libraries.versioning.model.VersionDTO;
import org.springframework.http.ResponseEntity;

/**
 * Created by iskitsas on 5/3/17.
 */
public interface CspClient {
    VersionDTO getApiVersion();
    ResponseEntity<String> postIntegrationData(IntegrationData integrationData, String context) throws InvalidDataTypeException;
    ResponseEntity<String> updateIntegrationData(IntegrationData integrationData, String context);
    ResponseEntity<String> deleteIntegrationData(IntegrationData integrationData, String context);
}
