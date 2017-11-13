package com.intrasoft.csp.misp.api;

import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.misp.commons.config.ApiContextUrl;
import com.intrasoft.csp.misp.commons.config.MispContextUrl;
import com.intrasoft.csp.misp.service.AdapterDataHandler;
import com.intrasoft.csp.misp.service.EmitterDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

public class EmitterController implements ApiContextUrl {

    private static final Logger LOG = LoggerFactory.getLogger(EmitterController.class);

    @Autowired
    EmitterDataHandler emitterDataHandler;

    @RequestMapping(value = API_BASE + "v/" + REST_API_V1 + "/" + API_EMITTER,
            consumes = {"application/json"},
            method = RequestMethod.POST)
    public void synchNewIntData(@RequestBody IntegrationData integrationData) {
        LOG.info("MISP Endpoint: POST received");
        try {
            emitterDataHandler.handleMispData(integrationData, MispContextUrl.MispEntity.EVENT);
        } catch (IOException e) {

        }
    }
}
