package com.intrasoft.csp.anon.api;
import com.intrasoft.csp.anon.commons.model.AnonContextUrl;
import com.intrasoft.csp.anon.commons.model.IntegrationAnonData;
import com.intrasoft.csp.anon.service.ApiDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;



@RestController
public class AnonController implements AnonContextUrl {

    private static final Logger LOG = LoggerFactory.getLogger(AnonController.class);

    @Autowired
    ApiDataHandler apiDataHandler;

    @RequestMapping(value = "/v"+REST_API_V1+"/"+DATA_ANONYMIZATION,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.POST)
    public ResponseEntity anonData(@RequestBody IntegrationAnonData integrationAnonData) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
        LOG.info("Anon Endpoint: POST received");
        return apiDataHandler.handleAnonIntegrationData(integrationAnonData);
    }


}
