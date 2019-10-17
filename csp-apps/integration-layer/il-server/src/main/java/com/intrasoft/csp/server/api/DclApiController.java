package com.intrasoft.csp.server.api;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class DclApiController implements CamelRoutes,ContextUrl{

    private static final Logger LOG = LoggerFactory.getLogger(DclApiController.class);

    @Autowired
    RouteUtils routes;

    @Produce
    private ProducerTemplate producerTemplate;

    @Autowired
    ApiDataHandler apiDataHandler;

    /**
     *
     * @param integrationData
     * @return
     */
    @RequestMapping(value = "/v"+REST_API_V1+"/"+DCL_INTEGRATION_DATA,
            consumes = {"application/json"},
            method = RequestMethod.POST)
    public ResponseEntity<String> getNewIntDataFromExtCsp(@RequestBody IntegrationData integrationData,@RequestHeader HttpHeaders headers) {
        LOG.info("DCL API: POST received with headers {}",headers);
        LOG.debug("DCL API: type {} origin {}", integrationData.getDataType(), integrationData.getDataParams().getOriginCspId());
        apiDataHandler.checkIsValidCspIdAgainstCertificateHeader(headers,integrationData);
        return handleIntegrationData(integrationData, "POST");
    }

    /**
     *
     * @param integrationData
     * @return
     */
    @RequestMapping(value = "/v"+REST_API_V1+"/"+DCL_INTEGRATION_DATA,
            consumes = {"application/json"},
            method = RequestMethod.PUT)
    public ResponseEntity<String> getUpdateIntDataFromExtCsp(@RequestBody IntegrationData integrationData,@RequestHeader HttpHeaders headers) {
        LOG.info("DCL API: PUT received with headers {}",headers);
        LOG.debug("DCL API: type {} origin {}", integrationData.getDataType(), integrationData.getDataParams().getOriginCspId());
        apiDataHandler.checkIsValidCspIdAgainstCertificateHeader(headers,integrationData);
        return handleIntegrationData(integrationData, "PUT");
    }


    private ResponseEntity<String> handleIntegrationData(IntegrationData integrationData ,String requestMethod){
        return apiDataHandler.handleIntegrationData(routes.wrap(EDCL),integrationData,requestMethod);
    }
}
