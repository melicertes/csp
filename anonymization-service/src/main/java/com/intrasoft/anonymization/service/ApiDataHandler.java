package com.intrasoft.anonymization.service;

import com.intrasoft.anonymization.api.HttpStatusResponseType;
import com.intrasoft.anonymization.model.IntegrationAnonData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ApiDataHandler {

    public ResponseEntity<String> handleAnonIntegrationData(IntegrationAnonData integrationAnonData) {

        // @TODO Handle integrationData, send IntegrationData to the anonymization service and receive anonymized intagrationData
        return new ResponseEntity<String>(HttpStatusResponseType.SUCCESSFUL_OPERATION.getReasonPhrase(),
                HttpStatus.OK);
    }
}
