package com.sastix.csp.api;

import com.sastix.csp.exceptions.InvalidDataTypeException;
import com.sastix.csp.model.IntegrationData;
import com.sastix.csp.model.IntegrationDataType;
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
public class DslApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DslApiController.class);

    @Produce
    private ProducerTemplate intDataProducer;

    @RequestMapping(value = "/dsl",
            consumes = {"application/json"},
            method = RequestMethod.POST)
    public ResponseEntity<String> synchNewIntData(@RequestBody IntegrationData newIntDataObj) {

        try {
            String dataType = getDataType(newIntDataObj.getDataType());

            if (dataType != null) {
                intDataProducer.sendBody("direct:" + dataType, newIntDataObj);
                intDataProducer.sendBody("direct:ddl", newIntDataObj);
            } else {
                throw new InvalidDataTypeException();
            }

        } catch (InvalidDataTypeException e) {
            LOGGER.warn(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/dsl",
            consumes = {"application/json"},
            method = RequestMethod.PUT)
    public ResponseEntity<String> synchUpdatedIntData(@RequestBody IntegrationData updIntDataObj) {

        try {
            String dataType = getDataType(updIntDataObj.getDataType());

            if (dataType != null) {
                intDataProducer.sendBody("direct:" + dataType, updIntDataObj);
                intDataProducer.sendBody("direct:ddl", updIntDataObj);
            } else {
                throw new InvalidDataTypeException();
            }
        } catch (InvalidDataTypeException e) {
            LOGGER.warn(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String getDataType(IntegrationDataType dataType) throws InvalidDataTypeException {

        if (dataType != null) {
            if (dataType == IntegrationDataType.VULNERABILITY) {
                return "vulnerability";
            } else if (dataType == IntegrationDataType.ARTEFACT) {
                return "artefact";
            } else if (dataType == IntegrationDataType.THREAT) {
                return "threat";
            } else if (dataType == IntegrationDataType.INCIDENT) {
                return "incident";
            } else if (dataType == IntegrationDataType.FILE) {
                return "file";
            } else if (dataType == IntegrationDataType.CONTACT) {
                return "contact";
            } else if (dataType == IntegrationDataType.CHAT) {
                return "chat";
            } else {
                return null;
            }
        }
        return null;
    }

}
