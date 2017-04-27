package com.sastix.csp.server.processors;

import com.sastix.csp.commons.constants.AppProperties;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.IntegrationDataType;
import com.sastix.csp.commons.routes.CamelRoutes;
import com.sastix.csp.commons.routes.HeaderName;
import com.sastix.csp.server.routes.RouteUtils;
import com.sastix.csp.server.service.CspUtils;
import org.apache.camel.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class DslProcessor implements Processor,CamelRoutes {

    @Produce
    ProducerTemplate producerTemplate;

    @Autowired
    CspUtils cspUtils;

    @Autowired
    Environment env;

    @Autowired
    RouteUtils routes;

    private static final Logger LOG = LoggerFactory.getLogger(DslProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        IntegrationData integrationData = cspUtils.getExchangeData(exchange, IntegrationData.class);
        List<String> recipients = computeRecipientsApps(exchange, integrationData);
        exchange.getIn().setHeader("recipients", recipients);

    }

    private List<String> computeRecipientsApps(Exchange exchange, IntegrationData integrationData) {
        List<String> recipients = new ArrayList<>();
        IntegrationDataType dataType = integrationData.getDataType();
        Boolean isExternal = integrationData.getSharingParams().getIsExternal();

        List<String> apps = new ArrayList<>();
        String appsStr = env.getProperty((isExternal? AppProperties.EXTERNAL:AppProperties.INTERNAL)+"."+dataType.toString()+".apps");

        if(!StringUtils.isEmpty(appsStr)){
            String[] appsArr =  appsStr.split(",");
            apps = Arrays.asList(appsArr).stream().map(s->s.trim()).collect(Collectors.toList());
        }

        if(!isExternal){
            recipients.add(routes.apply(DDL));
        }

        for (String app : apps) {
            //String uri = cspUtils.getAppUri(app);
            //recipients.add(uri);
            //recipients.add(CamelRoutes.APP+"?name="+app);//haven't find a solution for this yet, using producerTemplate instead
            Map<String,Object> headers = new HashMap<>();
            headers.put(HeaderName.APP_NAME,app);
            headers.put(Exchange.HTTP_METHOD,exchange.getIn().getHeader(Exchange.HTTP_METHOD));
            producerTemplate.sendBodyAndHeaders(routes.apply(APP), ExchangePattern.InOut,integrationData, headers);
        }

        return recipients;
    }
}
