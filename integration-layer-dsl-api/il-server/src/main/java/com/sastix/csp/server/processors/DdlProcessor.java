package com.sastix.csp.server.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.IntegrationDataType;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class DdlProcessor implements Processor {
    @Autowired
    ObjectMapper objectMapper;

    @Value("${elastic.uri}")
    String elasticURI;

    @Override
    public void process(Exchange exchange) throws Exception {

        //IntegrationData integrationData = exchange.getIn().getBody(IntegrationData.class);
        String inData = exchange.getIn().getBody(String.class);


//        ObjectMapper om = new ObjectMapper();
        IntegrationData integrationData = objectMapper.readValue(inData, IntegrationData.class);


        exchange.getContext().getProperties().get("http.proxyPort");

//System.out.println("====="+exchange.filter(header("Exchange.HTTP_METHOD").isEqualTo("GET")) );
        /*
        DDL indexes data (DDL -> ELASTIC API)
         */
        IntegrationDataType indexType = integrationData.getDataType();
        Object dataObject = integrationData.getDataObject();
        Boolean toShare = integrationData.getSharingParams().getToShare();

        List<String> recipients = new ArrayList<String>();

        /**
         * @TODO: Match indices with Andreas
         */
        //String es = elasticURI + "/viper/" + indexType.toString().toLowerCase() + "?pretty";

        if (toShare)
            recipients.add("direct:dcl");

        recipients.add(elasticURI + "/viper/" + indexType.toString().toLowerCase() + "?pretty");

        exchange.getIn().setHeader("recipients", recipients);
        //exchange.getIn().setHeader("elastic", es);

        /**
         * @TODO: dataObject OR integrationData
         */
        exchange.getIn().setBody(dataObject);

    }
}
