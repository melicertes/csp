package com.sastix.csp.server.service;

import com.sastix.csp.commons.routes.CamelRoutes;
import com.sastix.csp.server.routes.RouteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Created by iskitsas on 5/10/17.
 */
@Component
public class ServerInitializer implements ApplicationRunner,CamelRoutes{
    private static final Logger LOG = LoggerFactory.getLogger(ServerInitializer.class);

    @Value("${consume.errorq.onstartup}")
    Boolean consumeErrorqOnstartup;

    @Autowired
    ErrorMessageHandler errorMessageHandler;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        LOG.info("consume.errorq.onstartup = "+consumeErrorqOnstartup);
        if(consumeErrorqOnstartup) {
            errorMessageHandler.consumeErrorMessagesOnStartUp();
        }
    }
}
