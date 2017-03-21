package com.sastix.csp.server.api;

import com.sastix.csp.commons.apiHttpStatusResponse.HttpStatusResponseType;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(DslApiController.class);

    @Produce
    private ProducerTemplate intDataProducer;

    @RequestMapping(value = "/dsl/integrationData",
            consumes = {"application/json"},
            method = RequestMethod.POST)
    public ResponseEntity<String> synchNewIntData(@RequestBody IntegrationData intDataObj) {

        try {
            String dataType = getDataType(intDataObj.getDataType());

            if (dataType != null) {
                if (!intDataObj.getSharingParams().getIsExternal()) {
                    intDataProducer.sendBody("direct:apps", intDataObj);
                }
                intDataProducer.sendBody("direct:ddl", intDataObj);
            } else {
                throw new InvalidDataTypeException();
            }

        } catch (InvalidDataTypeException e) {
            LOGGER.warn(e.getMessage());
            return new ResponseEntity<>(HttpStatusResponseType.MALFORMED_INTEGRATION_DATA_STRUCTURE.getReasonPhrase(),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatusResponseType.SUCCESSFUL_OPERATION.getReasonPhrase(),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/dsl/integrationData",
            consumes = {"application/json"},
            method = RequestMethod.PUT)
    public ResponseEntity<String> synchUpdatedIntData(@RequestBody IntegrationData intDataObj) {

        try {
            String dataType = getDataType(intDataObj.getDataType());

            if (dataType != null) {
                if (!intDataObj.getSharingParams().getIsExternal()) {
                    intDataProducer.sendBody("direct:apps", intDataObj);
                }
                intDataProducer.sendBody("direct:ddl", intDataObj);
            } else {
                throw new InvalidDataTypeException();
            }

        } catch (InvalidDataTypeException e) {
            LOGGER.warn(e.getMessage());
            return new ResponseEntity<>(HttpStatusResponseType.MALFORMED_INTEGRATION_DATA_STRUCTURE.getReasonPhrase(),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatusResponseType.SUCCESSFUL_OPERATION.getReasonPhrase(),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/dsl/integrationData",
            consumes = {"application/json"},
            method = RequestMethod.DELETE)
    public ResponseEntity<String> synchDeletedIntData(@RequestBody IntegrationData intDataObj) {

        try {
            String dataType = getDataType(intDataObj.getDataType());

            if (dataType != null) {
                if (!intDataObj.getSharingParams().getIsExternal()) {
                    intDataProducer.sendBody("direct:apps", intDataObj);
                }
                intDataProducer.sendBody("direct:ddl", intDataObj);
            } else {
                throw new InvalidDataTypeException();
            }

        } catch (InvalidDataTypeException e) {
            LOGGER.warn(e.getMessage());
            return new ResponseEntity<>(HttpStatusResponseType.MALFORMED_INTEGRATION_DATA_STRUCTURE.getReasonPhrase(),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatusResponseType.SUCCESSFUL_OPERATION.getReasonPhrase(),
                HttpStatus.OK);
    }

    private String getDataType(IntegrationDataType dataType) throws InvalidDataTypeException {

        if (dataType != null) {
            switch (dataType) {
                case EVENT:
                    return "event";
                case THREAT:
                    return "threat";
                case INCIDENT:
                    return "incident";
                case VULNERABILITY:
                    return "vulnerability";
                case ARTEFACT:
                    return "artefact";
                case CHAT:
                    return "chat";
                case FILE:
                    return "file";
                case CONTACT:
                    return "contact";
                case TRUSTCIRCLE:
                    return "trustCircle";
                default:
                    return null;
            }
        }
        return null;
    }
}
