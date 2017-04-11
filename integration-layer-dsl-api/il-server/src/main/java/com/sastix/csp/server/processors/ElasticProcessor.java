package com.sastix.csp.server.processors;

import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.IntegrationDataType;
import com.sastix.csp.server.service.CamelRestService;
import com.sastix.csp.server.service.CspUtils;
import org.apache.camel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by iskitsas on 4/11/17.
 */
@Component
public class ElasticProcessor implements Processor {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticProcessor.class);

    @Value("${elastic.uri}")
    String elasticUri;

    @Produce
    ProducerTemplate producerTemplate;

    @Autowired
    CamelRestService camelRestService;

    @Autowired
    CspUtils cspUtils;

    @Override
    public void process(Exchange exchange) throws Exception {
        //String contextParams = (String) exchange.getIn().getHeader(HeaderName.CONTEXT_PARAMS);
        IntegrationData integrationData = cspUtils.getExchangeData(exchange, IntegrationData.class);
        String httpMethod = (String) exchange.getIn().getHeader(Exchange.HTTP_METHOD);
        /*
        DDL indexes data (DDL -> ELASTIC API)
         */
        IntegrationDataType indexType = integrationData.getDataType();
        Object dataObject = integrationData.getDataObject();

        //TODO: Match indices with Andreas
        //String es = elasticURI + "/viper/" + indexType.toString().toLowerCase() + "?pretty";

        String contextParams = "/viper/" + indexType.toString().toLowerCase() + "?pretty";

        //TODO: dataObject OR integrationData
        //producerTemplate.sendBody(elasticUri+contextParams, ExchangePattern.InOut,integrationData);
        camelRestService.send(elasticUri+contextParams,integrationData, httpMethod);
        LOG.info("");
    }
}
