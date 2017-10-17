package com.intrasoft.csp.misp.service.impl;

import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.misp.service.AdapterDataHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ApiDataHandlerImpl implements AdapterDataHandler{
    @Override
    public ResponseEntity<String> handleIntegrationData(IntegrationData integrationData, String requestMethod) {
        return null;
    }
}
