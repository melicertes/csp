package com.intrasoft.csp.misp.api;

import com.intrasoft.csp.libraries.model.IntegrationData;
import com.intrasoft.csp.misp.commons.config.ApiContextUrl;
import com.intrasoft.csp.misp.service.AdapterDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


public class AdapterController implements ApiContextUrl {

    private static final Logger LOG = LoggerFactory.getLogger(AdapterController.class);

    @Autowired
    AdapterDataHandler adapterDataHandler;

    @RequestMapping(value = API_BASE + "v/" + REST_API_V1 + "/" + API_ADAPTER,
            consumes = {"application/json"},
            method = RequestMethod.POST)
    public ResponseEntity<String> synchNewIntData(@RequestBody IntegrationData integrationData) {
        LOG.info("MISP Endpoint: POST received");
        return adapterDataHandler.handleIntegrationData(integrationData, "POST");
    }

    @RequestMapping(value = API_BASE + "v/" + REST_API_V1 + "/" + API_ADAPTER,
            consumes = {"application/json"},
            method = RequestMethod.PUT)
    public ResponseEntity<String> synchUpdatedIntData(@RequestBody IntegrationData integrationData) {
        LOG.info("MISP Endpoint: PUT received");
        return adapterDataHandler.handleIntegrationData(integrationData, "PUT");
    }

    @RequestMapping(value = API_BASE + "v/" + REST_API_V1 + "/" + API_ADAPTER,
            consumes = {"application/json"},
            method = RequestMethod.DELETE)
    public ResponseEntity<String> synchDeletedIntData(@RequestBody IntegrationData integrationData) {
        LOG.info("MISP Endpoint: DELETE received");
        return adapterDataHandler.handleIntegrationData(integrationData, "DELETE");
    }
}
