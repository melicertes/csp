package com.sastix.csp.server.processors;

import com.sastix.csp.commons.exceptions.CspBusinessException;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.routes.HeaderName;
import com.sastix.csp.server.service.CamelRestService;
import com.sastix.csp.server.service.CspUtils;
import org.apache.camel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created by iskitsas on 4/10/17.
 */
@Component
public class AppProcessor implements Processor{
    private static final Logger LOG = LoggerFactory.getLogger(AppProcessor.class);

    @Produce
    ProducerTemplate producerTemplate;

    @Autowired
    CspUtils cspUtils;

    @Autowired
    CamelRestService camelRestService;

    @Override
    public void process(Exchange exchange) throws Exception {
        String appName = (String) exchange.getIn().getHeader(HeaderName.APP_NAME);
        IntegrationData integrationData = cspUtils.getExchangeData(exchange, IntegrationData.class);
        String httpMethod = (String) exchange.getIn().getHeader(Exchange.HTTP_METHOD);

        String appUri = cspUtils.getAppUri(appName);
        if(!StringUtils.isEmpty(appUri)){
            LOG.info("DSL - Send to internal application: " + appName + " - " + appUri);
            camelRestService.send(appUri,integrationData, httpMethod);
        }else{
            //handle situation
            String msg = "App processor - could not send to app uri - app not found. Provided appName="+appName;
            LOG.error(msg);
            // If we want to use the guaranteed delivery mechanism we should throw the exception
            //throw new CspBusinessException(msg);
        }

    }
}
