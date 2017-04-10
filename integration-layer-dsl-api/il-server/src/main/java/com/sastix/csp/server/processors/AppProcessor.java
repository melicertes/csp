package com.sastix.csp.server.processors;

import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.routes.ContextUrl;
import com.sastix.csp.commons.routes.HeaderName;
import com.sastix.csp.server.service.CspUtils;
import org.apache.camel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

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

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String,Object> map = exchange.getIn().getHeaders();
        String appName = (String) exchange.getIn().getHeader(HeaderName.APP_NAME);
        IntegrationData integrationData = exchange.getIn().getBody(IntegrationData.class);

        String appUri = cspUtils.getAppUri(appName);
        if(!StringUtils.isEmpty(appUri)){
            //producerTemplate.sendBody(appUri, ExchangePattern.InOut,integrationData);
        }else{
            //TODO: handle situation
            LOG.warn("could not send to app uri - app not found.");
        }

        LOG.info("");
    }
}
