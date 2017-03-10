package com.sastix.csp.server.processors;

import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.IntegrationDataType;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class RecipientsProcessor implements Processor {

    private static HashMap<String, String> dataTypesAppMapping = new HashMap<>();
    private static final String DELIMITER = ",";
    private static final String APPLICATION_ADAPRTER_URI = "http://localhost:{{server.port}}/adapter/";

    static {
        dataTypesAppMapping.put("threat", "App1,App2,App3,App4");
        dataTypesAppMapping.put("incident", "App2");
        dataTypesAppMapping.put("vulnerability", "App3,App4,App5");
        dataTypesAppMapping.put("artefact", "App2,App3,App4,App5");
        dataTypesAppMapping.put("chat", "App1,App2,App5");
        dataTypesAppMapping.put("file", "App3,App4");
        dataTypesAppMapping.put("contact", "App5");
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        IntegrationData integrationData = exchange.getIn().getBody(IntegrationData.class);

        List<String> recipients = new ArrayList<String>();

        IntegrationDataType dataType = integrationData.getDataType();

        switch (dataType) {
            case THREAT:
                computeRecipientsApps(recipients, dataType);
                break;
            case INCIDENT:
                computeRecipientsApps(recipients, dataType);
                break;
            case VULNERABILITY:
                computeRecipientsApps(recipients, dataType);
                break;
            case ARTEFACT:
                computeRecipientsApps(recipients, dataType);
                break;
            case CHAT:
                computeRecipientsApps(recipients, dataType);
                break;
            case FILE:
                computeRecipientsApps(recipients, dataType);
                break;
            case CONTACT:
                computeRecipientsApps(recipients, dataType);
                break;
        }

        exchange.getIn().setHeader("recipients", recipients);

    }

    private void computeRecipientsApps(List<String> recipients, IntegrationDataType dataType) {
        String[] suppApps = dataTypesAppMapping.get(dataType.toString()).split(DELIMITER);
        for (String app : suppApps) {
            recipients.add(APPLICATION_ADAPRTER_URI + app);
        }
    }
}
