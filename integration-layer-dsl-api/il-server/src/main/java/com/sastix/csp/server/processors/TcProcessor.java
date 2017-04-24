package com.sastix.csp.server.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sastix.csp.client.TrustCirclesClient;
import com.sastix.csp.commons.model.Csp;
import com.sastix.csp.commons.model.IntegrationDataType;
import com.sastix.csp.commons.model.TrustCircle;
import com.sastix.csp.commons.model.TrustCircleEcspDTO;
import com.sastix.csp.commons.routes.ContextUrl;
import com.sastix.csp.server.service.CamelRestService;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by iskitsas on 4/9/17.
 */
@Component
public class TcProcessor implements Processor{
    private static final Logger LOG = LoggerFactory.getLogger(TcProcessor.class);

    @Value("${tc.protocol}")
    String tcProtocol;
    @Value("${tc.host}")
    String tcHost;
    @Value("${tc.port}")
    String tcPort;
    @Value("${tc.path.circles}")
    String tcPath;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CamelRestService camelRestService;

    @Autowired
    TrustCirclesClient tcClient;

    @Override
    public void process(Exchange exchange) throws Exception {
//        TrustCircleEcspDTO trustCircleEcspDTO = exchange.getIn().getBody(TrustCircleEcspDTO.class);
        Csp csp = exchange.getIn().getBody(Csp.class);
        String httpMethod = (String) exchange.getIn().getHeader(Exchange.HTTP_METHOD);

        String uri = this.getTcURI() + "/" + csp.getCspId();
        LOG.info(uri);
        TrustCircle tc = camelRestService.send(uri, csp, httpMethod, TrustCircle.class);
        LOG.info(tc.toString());
        Message m = new DefaultMessage();
        m.setBody(tc);
        exchange.setOut(m);
    }

/*    private String getThreatVal(Csp csp) {
        String shortNameVal = "CTC::";
        if (csp.getCspId().equals(IntegrationDataType.ARTEFACT.name())) {
            shortNameVal += "SHARING_DATA_ARTEFACT";
        } else if (csp.getCspId().equals(IntegrationDataType.CHAT.name())) {
            shortNameVal += "SHARING_DATA_CHAT";
        } else if (csp.getCspId().equals(IntegrationDataType.VULNERABILITY.name())) {
            shortNameVal += "SHARING_DATA_VULNERABILITY";
        } else if (csp.getCspId().equals(IntegrationDataType.CONTACT.name())) {
            shortNameVal += "SHARING_DATA_CONTACT";
        } else if (csp.getCspId().equals(IntegrationDataType.EVENT.name())) {
            shortNameVal += "SHARING_DATA_EVENT";
        } else if (csp.getCspId().equals(IntegrationDataType.FILE.name())) {
            shortNameVal += "SHARING_DATA_FILE";
        } else if (csp.getCspId().equals(IntegrationDataType.INCIDENT.name())) {
            shortNameVal += "SHARING_DATA_INCIDENT";
        } else if (csp.getCspId().trim().equals(IntegrationDataType.THREAT.name().trim())) {
            shortNameVal += "SHARING_DATA_THREAT";
        } else if (csp.getCspId().equals(IntegrationDataType.TRUSTCIRCLE.name())) {
            shortNameVal += "";
        } else {
            shortNameVal += "UNKNOWN";
        }
        LOG.info(shortNameVal);
        return shortNameVal;
    }*/

    private String getTcURI() {
        return tcProtocol + "://" + tcHost + ":" + tcPort + tcPath;
    }
}
