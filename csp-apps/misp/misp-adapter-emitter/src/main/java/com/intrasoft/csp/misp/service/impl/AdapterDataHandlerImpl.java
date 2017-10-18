package com.intrasoft.csp.misp.service.impl;

import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.misp.service.AdapterDataHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AdapterDataHandlerImpl implements AdapterDataHandler{
    @Override
    public ResponseEntity<String> handleIntegrationData(IntegrationData integrationData, String requestMethod) {

        //TODO process and post integration data to MISP API

        return null;
    }
}
