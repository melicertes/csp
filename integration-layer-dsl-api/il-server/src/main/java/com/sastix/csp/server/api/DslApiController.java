package com.sastix.csp.server.api;

import com.sastix.csp.commons.apiHttpStatusResponse.HttpStatusResponseType;
import com.sastix.csp.commons.exceptions.InvalidDataTypeException;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.routes.CamelRoutes;
import com.sastix.csp.server.routes.RouteUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DslApiController implements CamelRoutes{

    private static final Logger LOG = LoggerFactory.getLogger(DslApiController.class);

    @Produce
    private ProducerTemplate producerTemplate;

    @Autowired
    RouteUtils routes;


    @RequestMapping(value = "/dsl/integrationData",
            consumes = {"application/json"},
            method = RequestMethod.POST)
    public ResponseEntity<String> synchNewIntData(@RequestBody IntegrationData integrationData) {
        return handleIntegrationData(integrationData, "POST");
    }

    @RequestMapping(value = "/dsl/integrationData",
            consumes = {"application/json"},
            method = RequestMethod.PUT)
    public ResponseEntity<String> synchUpdatedIntData(@RequestBody IntegrationData integrationData) {
        return handleIntegrationData(integrationData, "PUT");
    }

    @RequestMapping(value = "/dsl/integrationData",
            consumes = {"application/json"},
            method = RequestMethod.DELETE)
    public ResponseEntity<String> synchDeletedIntData(@RequestBody IntegrationData integrationData) {
        return handleIntegrationData(integrationData, "DELETE");
    }

    private ResponseEntity<String> handleIntegrationData(IntegrationData integrationData ,String requestMethod){
        try {
            String dataType = integrationData.getDataType().toString();

            if (dataType != null) {
                producerTemplate.sendBodyAndHeader(routes.apply(DSL), integrationData, Exchange.HTTP_METHOD, requestMethod);
            } else {
                throw new InvalidDataTypeException();
            }

        } catch (InvalidDataTypeException e) {
            LOG.warn(e.getMessage());
            return new ResponseEntity<>(HttpStatusResponseType.MALFORMED_INTEGRATION_DATA_STRUCTURE.getReasonPhrase(),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatusResponseType.SUCCESSFUL_OPERATION.getReasonPhrase(),
                HttpStatus.OK);
    }
}
