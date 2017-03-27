package com.sastix.csp.server.api;

import com.sastix.csp.commons.exceptions.InvalidDataTypeException;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.IntegrationDataType;
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

    private static final Logger logger = LoggerFactory.getLogger(DslApiController.class);

    @Produce
    private ProducerTemplate intDataProducer;

    @RequestMapping(value = "/dsl",
            consumes = {"application/json"},
            method = RequestMethod.POST)
    public ResponseEntity<String> synchNewIntData(@RequestBody IntegrationData newIntDataObj) {

        try {
            String dataType = getDataType(newIntDataObj.getDataType());

            logger.info(newIntDataObj.toString());

            if (dataType != null) {
                intDataProducer.sendBody("direct:apps", newIntDataObj);
                intDataProducer.sendBody("direct:ddl", newIntDataObj);
            } else {
                throw new InvalidDataTypeException();
            }

        } catch (InvalidDataTypeException e) {
            logger.warn(e.getMessage());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @RequestMapping(value = "/dsl",
            consumes = {"application/json"},
            method = RequestMethod.PUT)
    public ResponseEntity<String> synchUpdatedIntData(@RequestBody IntegrationData updIntDataObj) {

        try {
            String dataType = getDataType(updIntDataObj.getDataType());

            if (dataType != null) {
                intDataProducer.sendBody("direct:apps", updIntDataObj);
                intDataProducer.sendBody("direct:ddl", updIntDataObj);
            } else {
                throw new InvalidDataTypeException();
            }
        } catch (InvalidDataTypeException e) {
            logger.warn(e.getMessage());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @RequestMapping(value = "/dsl",
            consumes = {"application/json"},
            method = RequestMethod.DELETE)
    public ResponseEntity<String> synchDeletedIntData(@RequestBody IntegrationData delIntDataObj) {

        try {
            String dataType = getDataType(delIntDataObj.getDataType());

            if (dataType != null) {
                intDataProducer.sendBody("direct:apps", delIntDataObj);
                intDataProducer.sendBody("direct:ddl", delIntDataObj);
            } else {
                throw new InvalidDataTypeException();
            }
        } catch (InvalidDataTypeException e) {
            logger.warn(e.getMessage());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    private String getDataType(IntegrationDataType dataType) throws InvalidDataTypeException {

        if (dataType != null) {
            switch (dataType) {
                case VULNERABILITY:
                    return "vulnerability";
                case ARTEFACT:
                    return "artefact";
                case THREAT:
                    return "threat";
                case INCIDENT:
                    return "incident";
                case FILE:
                    return "file";
                case CONTACT:
                    return "contact";
                case CHAT:
                    return "chat";
                default:
                    return null;
            }
        }
        return null;
    }
}
