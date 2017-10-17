package com.intrasoft.csp.misp.service;

import com.intrasoft.csp.commons.model.IntegrationData;
import org.springframework.http.ResponseEntity;


public interface AdapterDataHandler {

    public ResponseEntity<String> handleIntegrationData(IntegrationData integrationData, String requestMethod);
}
