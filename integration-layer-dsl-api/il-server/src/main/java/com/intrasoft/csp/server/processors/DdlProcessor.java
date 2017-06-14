package com.intrasoft.csp.server.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.routes.CamelRoutes;
import com.intrasoft.csp.server.routes.RouteUtils;
import com.intrasoft.csp.server.service.CspUtils;
import org.apache.camel.*;
import org.apache.camel.http.common.HttpMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class DdlProcessor implements Processor,CamelRoutes {

    private static final Logger LOG = LoggerFactory.getLogger(DdlProcessor.class);

    @Autowired
    ObjectMapper objectMapper;

    @Produce
    ProducerTemplate producerTemplate;

    @Autowired
    RouteUtils routes;

    @Autowired
    CspUtils cspUtils;

    @Value("${enableElastic}")
    Boolean enableElastic;

    @Override
    public void process(Exchange exchange) throws Exception {
        IntegrationData integrationData = cspUtils.getExchangeData(exchange, IntegrationData.class);
        Boolean toShare = integrationData.getSharingParams().getToShare();

        List<String> recipients = new ArrayList<>();

        if (toShare) {
            LOG.info("DDL - received integrationData with datatype: " + integrationData.getDataType() + ", toShare = true, sending to DCL" );
            if (!exchange.getIn().getHeader(Exchange.HTTP_METHOD).toString().equals(HttpMethods.DELETE.toString())){
                LOG.info(exchange.getIn().getHeader(Exchange.HTTP_METHOD).toString());
                recipients.add(routes.apply(DCL));}
                //producerTemplate.sendBodyAndHeader(routes.apply(DCL), ExchangePattern.InOut,integrationData, Exchange.HTTP_METHOD, httpMethod);
        }

        if (enableElastic){
            recipients.add(routes.apply(ELASTIC));//TODO: if trustCircle, it should not send it to Elastic
            //producerTemplate.sendBodyAndHeader(routes.apply(ELASTIC), ExchangePattern.InOut,integrationData, Exchange.HTTP_METHOD, httpMethod);
        }

        exchange.getIn().setHeader("recipients", recipients);
        //exchange.getIn().setHeader(Exchange.HTTP_METHOD,httpMethod);
        exchange.getIn().setBody(integrationData);
    }
}
