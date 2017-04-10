package com.sastix.csp.server.processors;

import com.sastix.csp.commons.model.TrustCircle;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by iskitsas on 4/10/17.
 */
@Component
public class EcspProcessor implements Processor{
    @Override
    public void process(Exchange exchange) throws Exception {
        TrustCircle tc = exchange.getIn().getBody(TrustCircle.class);
        List<String> ecsps = tc.getCsps();
        exchange.getIn().setHeader("ecsps", ecsps);
    }
}
