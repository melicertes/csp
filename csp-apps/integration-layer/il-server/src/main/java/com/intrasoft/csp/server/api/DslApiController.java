package com.intrasoft.csp.server.api;

import com.intrasoft.csp.commons.apiHttpStatusResponse.HttpStatusResponseType;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.routes.CamelRoutes;
import com.intrasoft.csp.commons.routes.ContextUrl;
import com.intrasoft.csp.server.routes.RouteUtils;
import com.intrasoft.csp.server.service.ApiDataHandler;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DslApiController implements CamelRoutes, ContextUrl{

    private static final Logger LOG = LoggerFactory.getLogger(DslApiController.class);

    @Produce
    private ProducerTemplate producerTemplate;

    @Autowired
    RouteUtils routes;

    @Autowired
    SpringValidatorAdapter springValidatorAdapter;

    @Autowired
    ApiDataHandler apiDataHandler;


    @RequestMapping(value = "/v"+REST_API_V1+"/"+DSL_INTEGRATION_DATA,
            consumes = {"application/json"},
            method = RequestMethod.POST)
    public ResponseEntity<String> synchNewIntData(@RequestBody IntegrationData integrationData) {
        LOG.info("DSL Endpoint: POST received");
        return handleIntegrationData(integrationData, "POST");
    }

    @RequestMapping(value = "/v"+REST_API_V1+"/"+DSL_INTEGRATION_DATA,
            consumes = {"application/json"},
            method = RequestMethod.PUT)
    public ResponseEntity<String> synchUpdatedIntData(@RequestBody IntegrationData integrationData) {
        LOG.info("DSL Endpoint: PUT received");
        return handleIntegrationData(integrationData, "PUT");
    }

    @RequestMapping(value = "/v"+REST_API_V1+"/"+DSL_INTEGRATION_DATA,
            consumes = {"application/json"},
            method = RequestMethod.DELETE)
    public ResponseEntity<String> synchDeletedIntData(@RequestBody IntegrationData integrationData) {
        LOG.info("DSL Endpoint: DELETE received");
        return handleIntegrationData(integrationData, "DELETE");
    }

    private ResponseEntity<String> handleIntegrationData(IntegrationData integrationData ,String requestMethod){
        return apiDataHandler.handleIntegrationData(routes.apply(DSL),integrationData,requestMethod);
    }
}
