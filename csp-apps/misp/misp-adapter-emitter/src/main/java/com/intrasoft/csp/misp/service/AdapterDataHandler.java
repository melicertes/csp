package com.intrasoft.csp.misp.service;

import com.intrasoft.csp.libraries.model.IntegrationData;
import org.springframework.http.ResponseEntity;

public interface AdapterDataHandler {

    public ResponseEntity<String> handleIntegrationData(IntegrationData integrationData, String requestMethod);
}
