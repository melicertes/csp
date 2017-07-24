package com.intrasoft.csp.anon.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.anon.model.IntegrationAnonData;
import com.intrasoft.csp.anon.service.ApiDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@RestController
public class AnonController {

    private static final Logger LOG = LoggerFactory.getLogger(AnonController.class);

    @Autowired
    ApiDataHandler apiDataHandler;

    @RequestMapping(value = "anon",
            consumes = {"application/json"},
            produces = {"application/json"},
            method = RequestMethod.POST)
    public ResponseEntity<String> anonNewIntData(@RequestBody String postData) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
        LOG.info("Anon Endpoint: POST received");
        ObjectMapper mapper = new ObjectMapper();
        IntegrationData integrationData = mapper.readValue(postData, IntegrationData.class);
        IntegrationAnonData integrationAnonData = new IntegrationAnonData();
        integrationAnonData.setIntegrationData(integrationData);
        integrationAnonData.setCspId(integrationData.getDataParams().getCspId());
        integrationAnonData.setDataType(integrationData.getDataType().toString());
        return apiDataHandler.handleAnonIntegrationData(integrationAnonData);
    }


}
