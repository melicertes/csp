package com.sastix.csp.server.processors;

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
    private static final Logger LOG = LoggerFactory.getLogger(TcProcessor.class);

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
            //producerTemplate.sendBody(appUri, ExchangePattern.InOut,integrationData);
            camelRestService.send(appUri,integrationData, httpMethod);
        }else{
            //TODO: handle situation
            LOG.warn("could not send to app uri - app not found.");
        }

    }
}
