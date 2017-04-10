package com.sastix.csp.server.processors;

import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.TrustCircle;
import com.sastix.csp.commons.model.TrustCircleEcspDTO;
import org.apache.camel.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by iskitsas on 4/10/17.
 */
@Component
public class EcspProcessor implements Processor{
    @Produce
    ProducerTemplate producerTemplate;

    @Override
    public void process(Exchange exchange) throws Exception {
        TrustCircleEcspDTO trustCircleEcspDTO = exchange.getIn().getBody(TrustCircleEcspDTO.class);
        TrustCircle tc = trustCircleEcspDTO.getTrustCircle();
        IntegrationData integrationData = trustCircleEcspDTO.getIntegrationData();
        List<String> ecsps = tc.getCsps();
        for (String ecsp : ecsps) {
            producerTemplate.sendBody(ecsp, ExchangePattern.OutIn,integrationData);
        }
        //exchange.getIn().setHeader("ecsps", ecsps);
    }
}
