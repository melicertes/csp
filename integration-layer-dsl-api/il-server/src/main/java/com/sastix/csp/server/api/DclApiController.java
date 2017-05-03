package com.sastix.csp.server.api;

import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.routes.CamelRoutes;
import com.sastix.csp.commons.routes.ContextUrl;
import com.sastix.csp.server.routes.RouteUtils;
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
public class DclApiController implements CamelRoutes,ContextUrl{

    private static final Logger LOG = LoggerFactory.getLogger(DclApiController.class);

    @Autowired
    RouteUtils routes;

    @Produce
    private ProducerTemplate intDataProducer;

    /**
     *
     * @param newIntDataObj
     * @return
     */
    @RequestMapping(value = "/v"+REST_API_V1+"/"+DCL_INTEGRATION_DATA,
            consumes = {"application/json"},
            method = RequestMethod.POST)
    public ResponseEntity<String> getNewIntDataFromExtCsp(@RequestBody IntegrationData newIntDataObj) {

        LOG.info(newIntDataObj.toString());
        intDataProducer.sendBodyAndHeader(routes.apply(EDCL), newIntDataObj,"method", "POST");

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     *
     * @param newIntDataObj
     * @return
     */
    @RequestMapping(value = "/v"+REST_API_V1+"/"+DCL_INTEGRATION_DATA,
            consumes = {"application/json"},
            method = RequestMethod.PUT)
    public ResponseEntity<String> getUpdateIntDataFromExtCsp(@RequestBody IntegrationData newIntDataObj) {

        LOG.info(newIntDataObj.toString());
        intDataProducer.sendBodyAndHeader(routes.apply(EDCL), newIntDataObj, "method", "PUT");

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
