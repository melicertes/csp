package com.sastix.csp.server.api;

import com.sastix.csp.commons.model.Csp;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.TrustCircle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TestDclApiController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @RequestMapping(value = "/ecsp/{cspId}",
            consumes = { "application/json" },
            method = RequestMethod.POST)
    public ResponseEntity<String> sendToExCsp(@RequestBody IntegrationData newIntDataObj, @PathVariable("cspId") String cspId) {
        logger.info("IntgrationData received by external CSP");
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @RequestMapping(value = "/tc",
            method = RequestMethod.POST)
    public ResponseEntity<TrustCircle> getTrustCircle(@RequestBody Csp csp) {

        List<String> csps = new ArrayList<>();
        csps.add("http://localhost:8081/ecsp/1");
        TrustCircle tc = new TrustCircle(csps);
        return new ResponseEntity<TrustCircle>(tc, HttpStatus.OK);
    }

}
