package com.intrasoft.anonymization.api;

import com.intrasoft.anonymization.model.IntegrationAnonData;
import com.intrasoft.anonymization.service.ApiDataHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
public class AnonController {

    private static final Logger LOG = LoggerFactory.getLogger(AnonController.class);

    @Autowired
    ApiDataHandler apiDataHandler;

    @RequestMapping(value = "anon",
            consumes = {"application/json"},
            method = RequestMethod.POST)
    public ResponseEntity<String> anonNewIntData(@RequestBody IntegrationAnonData integrationAnonData) {
        LOG.info("Anon Endpoint: POST received");
        return apiDataHandler.handleAnonIntegrationData(integrationAnonData);
    }


}
