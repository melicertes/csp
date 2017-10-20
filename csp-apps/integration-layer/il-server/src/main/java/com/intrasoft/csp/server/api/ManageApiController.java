package com.intrasoft.csp.server.api;

import com.intrasoft.csp.commons.routes.CamelRoutes;
import com.intrasoft.csp.commons.routes.ContextUrl;
import com.intrasoft.csp.server.service.ErrorMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by iskitsas on 5/10/17.
 */
@RestController
public class ManageApiController implements CamelRoutes, ContextUrl {
    private static final Logger LOG = LoggerFactory.getLogger(ManageApiController.class);

    @Autowired
    ErrorMessageHandler errorMessageHandler;


    @RequestMapping(value = "/v"+REST_API_V1+"/"+MANAGE_CONSUME_ERROR_Q+"/{maxMessagesToConsume}/{msDelay}",
            method = RequestMethod.GET)
    public ResponseEntity<String> consumeErrorQApi(@PathVariable Integer maxMessagesToConsume,@PathVariable Long msDelay) {
        String reportLB = "<br/>";
        String msg = "Consuming a max of "+maxMessagesToConsume+" messages with delay "+msDelay+"ms on each consumption ";
        String consumptionReport = errorMessageHandler.consumeErrorMessages(maxMessagesToConsume, msDelay,reportLB);
        StringBuilder ret = new StringBuilder();
        ret.append(msg).append(reportLB).append(reportLB).append(consumptionReport);
        return new ResponseEntity<>(ret.toString(), HttpStatus.OK);
    }
}
