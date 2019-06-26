package com.intrasoft.csp.server.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
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

        String jsonIntegrationData = objectMapper.writeValueAsString(integrationData);
        IntegrationData integrationDataCopy = objectMapper.readValue(jsonIntegrationData, IntegrationData.class);

        Boolean toShare = integrationData.getSharingParams().getToShare();
        String httpMethod = (String) exchange.getIn().getHeader(Exchange.HTTP_METHOD);
        List<String> recipients = new ArrayList<>();

        if (toShare) {
            LOG.info("DDL - received integrationData with datatype: " + integrationData.getDataType() + ", toShare = true, sending to DCL" );
            if (!exchange.getIn().getHeader(Exchange.HTTP_METHOD).toString().equals(HttpMethods.DELETE.toString())){
                LOG.info(exchange.getIn().getHeader(Exchange.HTTP_METHOD).toString());
                recipients.add(routes.wrap(DCL));
                //producerTemplate.sendBodyAndHeader(routes.wrap(DCL), ExchangePattern.InOut,integrationData, Exchange.HTTP_METHOD, httpMethod);
            }
        }

        if (enableElastic && !integrationData.getDataType().equals(IntegrationDataType.TRUSTCIRCLE)
                && !integrationData.getDataType().equals(IntegrationDataType.CONTACT)){
            //recipients.add(routes.wrap(ELASTIC));//Do not use this, because it share the exchange and will cause the bug described in SXCSP-430
            //Instead, use a copy of IntegrationData and send it using producer and HTTP
            //sync version
            //producerTemplate.sendBodyAndHeader(routes.wrap(ELASTIC), ExchangePattern.InOnly,integrationDataCopy, Exchange.HTTP_METHOD, httpMethod);
            //async version
            producerTemplate.asyncRequestBodyAndHeader(routes.wrap(ELASTIC),integrationDataCopy, Exchange.HTTP_METHOD, httpMethod);
        }

        exchange.getIn().setHeader("recipients", recipients);
        //exchange.getIn().setHeader(Exchange.HTTP_METHOD,httpMethod);
        exchange.getIn().setBody(integrationData);
    }
}
