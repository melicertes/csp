package com.intrasoft.csp.server.service;

import com.intrasoft.csp.commons.apiHttpStatusResponse.HttpStatusResponseType;
import com.intrasoft.csp.commons.exceptions.InvalidDataTypeException;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.routes.CamelRoutes;
import com.intrasoft.csp.commons.validators.IntegrationDataValidator;
import com.intrasoft.csp.server.routes.RouteUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

/**
 * Created by iskitsas on 6/10/17.
 */
@Service
public class ApiDataHandler implements CamelRoutes{
    private static final Logger LOG = LoggerFactory.getLogger(ApiDataHandler.class);

    @Autowired
    RouteUtils routes;

    @Autowired
    IntegrationDataValidator integrationDataValidator;

    @Produce
    private ProducerTemplate producerTemplate;

    public ResponseEntity<String> handleIntegrationData(String route, IntegrationData integrationData, String requestMethod) {
        BindingResult bindingResult = new BeanPropertyBindingResult(integrationData, "integrationData");
        integrationDataValidator.validate(integrationData, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new InvalidDataTypeException(bindingResult.getAllErrors().toString());
        }

        producerTemplate.sendBodyAndHeader(route, integrationData, Exchange.HTTP_METHOD, requestMethod);

        return new ResponseEntity<>(HttpStatusResponseType.SUCCESSFUL_OPERATION.getReasonPhrase(),
                HttpStatus.OK);
    }
}