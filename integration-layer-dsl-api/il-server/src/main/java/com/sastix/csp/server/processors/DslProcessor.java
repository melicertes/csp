package com.sastix.csp.server.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.IntegrationDataType;
import com.sastix.csp.commons.routes.CamelRoutes;
import com.sastix.csp.server.config.Flow1ApplicationsUrls;
import com.sastix.csp.server.config.Flow2ApplicationsUrls;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DslProcessor implements Processor {


    @Autowired
    ObjectMapper objectMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(DslProcessor.class);

    private static final String APPLICATION_ADAPTER_URI = "http://localhost:{{server.port}}/adapter/";

    private Flow1ApplicationsUrls flow1ApplicationsUrls;
    private Flow2ApplicationsUrls flow2ApplicationsUrls;

    @Autowired
    public void setFlow1ApplicationsUrls(Flow1ApplicationsUrls flow1ApplicationsUrls) {
        this.flow1ApplicationsUrls = flow1ApplicationsUrls;
    }

    @Autowired
    public void setFlow2ApplicationsUrls(Flow2ApplicationsUrls flow2ApplicationsUrls) {
        this.flow2ApplicationsUrls = flow2ApplicationsUrls;
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        IntegrationData integrationData;

        List<String> recipients = new ArrayList<String>();

        LOGGER.info(exchange.getIn().getHeaders().toString());

        integrationData = exchange.getIn().getBody(IntegrationData.class);
//        if (exchange.getFromEndpoint().getEndpointUri().equals("direct://edcl")) {
//            String inData = exchange.getIn().getBody(String.class);
//            integrationData = objectMapper.readValue(inData, IntegrationData.class);
//        }
//        else {
//            integrationData = exchange.getIn().getBody(IntegrationData.class);
//        }
        IntegrationDataType dataType = integrationData.getDataType();

        Boolean isExternal = integrationData.getSharingParams().getIsExternal();

        switch (dataType) {
            case THREAT:
                computeRecipientsApps(recipients, dataType, isExternal);
                break;
            case INCIDENT:
                computeRecipientsApps(recipients, dataType, isExternal);
                break;
            case VULNERABILITY:
                computeRecipientsApps(recipients, dataType, isExternal);
                break;
            case ARTEFACT:
                computeRecipientsApps(recipients, dataType, isExternal);
                break;
            case CHAT:
                computeRecipientsApps(recipients, dataType, isExternal);
                break;
            case FILE:
                computeRecipientsApps(recipients, dataType, isExternal);
                break;
            case CONTACT:
                computeRecipientsApps(recipients, dataType, isExternal);
                break;
        }

        exchange.getIn().setHeader("recipients", recipients);


        /*
        if (exchange.getFromEndpoint().getEndpointUri().equals("direct://edcl")) {
            String inData = exchange.getIn().getBody(String.class);
            integrationData = objectMapper.readValue(inData, IntegrationData.class);
        }
        else {
            integrationData = exchange.getIn().getBody(IntegrationData.class);
        }

        IntegrationDataType dataType = integrationData.getDataType();


        switch (dataType) {
            case THREAT:
                computeRecipientsApps(recipients, dataType, isExternal);
                break;
            case INCIDENT:
                computeRecipientsApps(recipients, dataType, isExternal);
                break;
            case VULNERABILITY:
                computeRecipientsApps(recipients, dataType, isExternal);
                break;
            case ARTEFACT:
                computeRecipientsApps(recipients, dataType, isExternal);
                break;
            case CHAT:
                computeRecipientsApps(recipients, dataType, isExternal);
                break;
            case FILE:
                computeRecipientsApps(recipients, dataType, isExternal);
                break;
            case CONTACT:
                computeRecipientsApps(recipients, dataType, isExternal);
                break;
        }

        Boolean isExternal = integrationData.getSharingParams().getIsExternal();
        if (!isExternal) {
            recipients.add(CamelRoutes.DDL);
        }
        exchange.getIn().setHeader("recipients", recipients);
*/

    }

    private void computeRecipientsApps(List<String> recipients, IntegrationDataType dataType, Boolean isExternal) {
        List<String> apps = new ArrayList<>();

        if (isExternal) {
            LOGGER.info("isExternal = {} => Flow 2 of integration layer (synch data from external CSPs), ", true);
            apps.addAll(flow2ApplicationsUrls.getAppsByDataType(dataType));
        } else {
            LOGGER.info("isExternal = {} => Flow 1 of integration layer (synch data from current CSP), ", false);
            recipients.add(CamelRoutes.DDL);
            apps.addAll(flow1ApplicationsUrls.getAppListByDataType(dataType));
        }

        for (String app : apps) {
            recipients.add(APPLICATION_ADAPTER_URI + app);
        }
    }

}
