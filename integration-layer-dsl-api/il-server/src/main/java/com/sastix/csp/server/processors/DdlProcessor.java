package com.sastix.csp.server.processors;

import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.IntegrationDataType;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class DdlProcessor implements Processor {

    @Value("${elastic.uri}")
    String elasticURI;

    @Override
    public void process(Exchange exchange) throws Exception {

        IntegrationData integrationData = exchange.getIn().getBody(IntegrationData.class);

        /*
        DDL indexes data (DDL -> ELASTIC API)
         */
        IntegrationDataType indexType = integrationData.getDataType();
        Object dataObject = integrationData.getDataObject();
        Boolean isExternal = integrationData.getSharingParams().getIsExternal();

        List<String> recipients = new ArrayList<String>();

        /**
         * @TODO: Match indices with Andreas
         */
        //String es = elasticURI + "/viper/" + indexType.toString().toLowerCase() + "?pretty";
        recipients.add(elasticURI + "/viper/" + indexType.toString().toLowerCase() + "?pretty");
        if (isExternal)
            recipients.add("direct:dcl");

        exchange.getIn().setHeader("recipients", recipients);
        //exchange.getIn().setHeader("elastic", es);

        /**
         * @TODO: dataObject OR integrationData
         */
        exchange.getIn().setBody(dataObject);

    }
}
