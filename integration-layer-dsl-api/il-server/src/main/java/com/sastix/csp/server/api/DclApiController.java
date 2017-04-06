package com.sastix.csp.server.api;

import com.sastix.csp.commons.exceptions.InvalidDataTypeException;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.IntegrationDataType;
import com.sastix.csp.commons.model.TrustCircle;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DclApiController {

    private static final Logger logger = LoggerFactory.getLogger(DslApiController.class);

    @Produce
    private ProducerTemplate intDataProducer;

    /**
     *
     * @param newIntDataObj
     * @return
     */
    @RequestMapping(value = "/dcl/integrationData",
            consumes = {"application/json"},
            method = RequestMethod.POST)
    public ResponseEntity<String> getNewIntDataFromExtCsp(@RequestBody IntegrationData newIntDataObj) {

        logger.info(newIntDataObj.toString());
        intDataProducer.sendBodyAndHeader("direct:edcl", newIntDataObj,"method", "POST");

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    /**
     *
     * @param newIntDataObj
     * @return
     */
    @RequestMapping(value = "/dcl/integrationData",
            consumes = {"application/json"},
            method = RequestMethod.PUT)
    public ResponseEntity<String> getUpdateIntDataFromExtCsp(@RequestBody IntegrationData newIntDataObj) {

        logger.info(newIntDataObj.toString());
        intDataProducer.sendBodyAndHeader("direct:edcl", newIntDataObj, "method", "PUT");

        return new ResponseEntity<String>(HttpStatus.OK);
    }

}
