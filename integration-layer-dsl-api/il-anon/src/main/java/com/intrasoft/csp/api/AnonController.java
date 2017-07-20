package com.intrasoft.csp.api;

import com.intrasoft.csp.model.IntegrationAnonData;
import com.intrasoft.csp.service.ApiDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@RestController
public class AnonController {

    private static final Logger LOG = LoggerFactory.getLogger(AnonController.class);

    @Autowired
    ApiDataHandler apiDataHandler;

    @RequestMapping(value = "anon",
            consumes = {"application/json"},
            method = RequestMethod.POST)
    public ResponseEntity<String> anonNewIntData(@RequestBody IntegrationAnonData integrationAnonData) throws InvalidKeyException, NoSuchAlgorithmException {
        LOG.info("Anon Endpoint: POST received");
        return apiDataHandler.handleAnonIntegrationData(integrationAnonData);
    }


}
