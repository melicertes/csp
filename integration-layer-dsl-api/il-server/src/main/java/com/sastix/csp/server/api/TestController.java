package com.sastix.csp.server.api;

import com.sastix.csp.commons.model.IntegrationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    @RequestMapping(value = "/adapter/{appId}",
            consumes = { "application/json" },
            method = RequestMethod.POST)
    public ResponseEntity<String> synchNewIntDataApps(@RequestBody IntegrationData newIntDataObj, @PathVariable("appId") String appId) {

        LOGGER.info("Appplication adapter receives new data for app with id {" + appId + "}. Datatype: " + newIntDataObj.getDataType().toString());
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @RequestMapping(value = "/ddl",
            consumes = { "application/json" },
            method = RequestMethod.POST)
    public ResponseEntity<String> synchNewIntDataDDL(@RequestBody IntegrationData newIntDataObj) {

        LOGGER.info("DDL receives new data from DSL. Datatype: " + newIntDataObj.getDataType().toString());
        return new ResponseEntity<String>(HttpStatus.OK);
    }

}
