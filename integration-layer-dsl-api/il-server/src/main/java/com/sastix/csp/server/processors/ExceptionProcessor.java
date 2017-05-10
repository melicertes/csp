package com.sastix.csp.server.processors;

import com.sastix.csp.commons.routes.CamelRoutes;
import com.sastix.csp.server.routes.RouteUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ExceptionProcessor implements Processor,CamelRoutes {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        String fromEndpointUri = exchange.getFromEndpoint().getEndpointUri();
        String exceptionCaught = exchange.getProperty("CamelExceptionCaught").toString();
        String msg = String.format("Exception '%s' caught at endpoint '%s' with body '%s'",
                exceptionCaught,fromEndpointUri,exchange.getIn().getBody().toString());

        //based on exception an additional logic could be implemented, eg send an email with errors

        Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        exchange.getIn().setHeader(Exchange.INTERCEPTED_ENDPOINT, fromEndpointUri);

        LOG.error(msg);
    }
}
