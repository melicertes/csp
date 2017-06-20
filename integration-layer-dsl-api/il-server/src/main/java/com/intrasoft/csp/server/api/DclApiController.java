package com.intrasoft.csp.server.api;

import com.intrasoft.csp.commons.apiHttpStatusResponse.HttpStatusResponseType;
import com.intrasoft.csp.commons.exceptions.InvalidDataTypeException;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.routes.CamelRoutes;
import com.intrasoft.csp.commons.routes.ContextUrl;
import com.intrasoft.csp.server.routes.RouteUtils;
import com.intrasoft.csp.server.service.ApiDataHandler;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DclApiController implements CamelRoutes,ContextUrl{

    private static final Logger LOG = LoggerFactory.getLogger(DclApiController.class);

    @Autowired
    RouteUtils routes;

    @Autowired
    SpringValidatorAdapter springValidatorAdapter;

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
    public ResponseEntity<String> getNewIntDataFromExtCsp(@RequestBody IntegrationData integrationData) {

        LOG.info("DCL Endpoint: POST received");

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
    public ResponseEntity<String> getUpdateIntDataFromExtCsp(@RequestBody IntegrationData integrationData) {

        LOG.info("DCL Endpoint: PUT received");

        return handleIntegrationData(integrationData, "PUT");
    }


    private ResponseEntity<String> handleIntegrationData(IntegrationData integrationData ,String requestMethod){
        return apiDataHandler.handleIntegrationData(routes.apply(EDCL),integrationData,requestMethod);
    }
}
