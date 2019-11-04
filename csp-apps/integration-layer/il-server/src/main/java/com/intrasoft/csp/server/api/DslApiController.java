package com.intrasoft.csp.server.api;

import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.routes.CamelRoutes;
import com.intrasoft.csp.commons.routes.ContextUrl;
import com.intrasoft.csp.server.routes.RouteUtils;
import com.intrasoft.csp.server.service.ApiDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class DslApiController implements CamelRoutes, ContextUrl{

    private static final Logger LOG = LoggerFactory.getLogger(DslApiController.class);

    @Autowired
    RouteUtils routes;

    @Autowired
    ApiDataHandler apiDataHandler;


    @RequestMapping(value = "/v"+REST_API_V1+"/"+DSL_INTEGRATION_DATA,
            consumes = {"application/json"},
            method = RequestMethod.POST)
    public ResponseEntity<String> syncNewIntData(@RequestBody IntegrationData integrationData, @RequestHeader HttpHeaders headers) {
        LOG.info("DSL API: POST received with headers {}",headers);
        LOG.debug("DSL API: type {} origin {} sharing {} ", integrationData.getDataType(), integrationData.getDataParams().getOriginCspId(), integrationData.getSharingParams());
        return handleIntegrationData(integrationData, "POST");
    }

    @RequestMapping(value = "/v"+REST_API_V1+"/"+DSL_INTEGRATION_DATA,
            consumes = {"application/json"},
            method = RequestMethod.PUT)
    public ResponseEntity<String> syncUpdatedIntData(@RequestBody IntegrationData integrationData, @RequestHeader HttpHeaders headers) {
        LOG.info("DSL API: PUT received with headers {}",headers);
        LOG.debug("DSL API: type {} origin {} sharing {} ", integrationData.getDataType(), integrationData.getDataParams().getOriginCspId(), integrationData.getSharingParams());
        return handleIntegrationData(integrationData, "PUT");
    }

    @RequestMapping(value = "/v"+REST_API_V1+"/"+DSL_INTEGRATION_DATA,
            consumes = {"application/json"},
            method = RequestMethod.DELETE)
    public ResponseEntity<String> syncDeletedIntData(@RequestBody IntegrationData integrationData, @RequestHeader HttpHeaders headers) {
        LOG.info("DSL API: DELETE received with headers {}",headers);
        LOG.debug("DSL API: type {} origin {}", integrationData.getDataType(), integrationData.getDataParams().getOriginCspId());
        return handleIntegrationData(integrationData, "DELETE");
    }

    private ResponseEntity<String> handleIntegrationData(IntegrationData integrationData ,String requestMethod){
        return apiDataHandler.handleIntegrationData(routes.wrap(DSL),integrationData,requestMethod);
    }
}
