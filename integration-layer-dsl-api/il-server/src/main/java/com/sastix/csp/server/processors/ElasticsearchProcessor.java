package com.sastix.csp.server.processors;

import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.IntegrationDataType;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;


@Component
public class ElasticsearchProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        IntegrationData integrationData = exchange.getIn().getBody(IntegrationData.class);

        /*
        DDL indexes data (DDL -> ELASTIC API)
         */
        IntegrationDataType indexType = integrationData.getDataType();
        String dataApp = integrationData.getDataApp();
        Object dataPayload = integrationData.getDataPaylod();

        exchange.getIn().setHeader("esURI", "/" + dataApp.toLowerCase() + "/" + indexType.toString().toLowerCase() + "?pretty");
        exchange.getIn().setBody(dataPayload);

    }
}
