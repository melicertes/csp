package com.sastix.csp.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExceptionProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        LOGGER.warn(exchange.getProperty("CamelExceptionCaught").toString());
    }
}
