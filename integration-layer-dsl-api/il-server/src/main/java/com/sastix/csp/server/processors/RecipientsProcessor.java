package com.sastix.csp.server.processors;

import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.IntegrationDataType;
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
public class RecipientsProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecipientsProcessor.class);

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

        IntegrationData integrationData = exchange.getIn().getBody(IntegrationData.class);

        List<String> recipients = new ArrayList<String>();

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

    }

    private void computeRecipientsApps(List<String> recipients, IntegrationDataType dataType, Boolean isExternal) {
        List<String> apps = new ArrayList<>();

        if (isExternal) {
            LOGGER.info("isExternal = {} => Flow 2 of integration layer (synch data from external CSPs), ", true);
            apps.addAll(flow2ApplicationsUrls.getAppsByDataType(dataType));
        } else {
            LOGGER.info("isExternal = {} => Flow 1 of integration layer (synch data from current CSP), ", false);
            apps.addAll(flow1ApplicationsUrls.getAppListByDataType(dataType));
        }

        for (String app : apps) {
            recipients.add(APPLICATION_ADAPTER_URI + app);
        }
    }
}
