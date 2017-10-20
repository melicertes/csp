package com.intrasoft.csp.server.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.routes.HeaderName;
import com.intrasoft.csp.server.service.CamelRestService;
import com.intrasoft.csp.server.service.CspUtils;
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

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void process(Exchange exchange) throws Exception {
        String appName = (String) exchange.getIn().getHeader(HeaderName.APP_NAME);
        IntegrationData integrationData = cspUtils.getExchangeData(exchange, IntegrationData.class);
        String httpMethod = (String) exchange.getIn().getHeader(Exchange.HTTP_METHOD);

        String appUri = cspUtils.getAppUri(appName);
        if(!StringUtils.isEmpty(appUri)){
            LOG.info("DSL - Send to internal application: " + appName + " - " + appUri);
            String json = objectMapper.writeValueAsString(integrationData);
            LOG.trace("---- Integration Data Object: \n\n"+json);
            camelRestService.send(appUri,integrationData, httpMethod, true);
        }else{
            //handle situation
            String msg = "App processor - could not send to app uri - app not found. Provided appName="+appName;
            LOG.error(msg);
            // If we want to use the guaranteed delivery mechanism we should throw the exception
            //throw new CspBusinessException(msg);
        }

    }
}
