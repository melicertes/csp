package com.sastix.csp.server.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExceptionProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        LOG.warn(exchange.getProperty("CamelExceptionCaught").toString());
    }
}
