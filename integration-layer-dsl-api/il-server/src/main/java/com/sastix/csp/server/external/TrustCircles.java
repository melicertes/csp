package com.sastix.csp.server.external;

import com.sastix.csp.commons.model.Csp;
import com.sastix.csp.commons.model.TrustCircle;
import com.sastix.csp.server.processors.EDclProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

public class TrustCircles {

    private static final Logger logger = LoggerFactory.getLogger(TrustCircles.class);
    private static final String TC_ADAPRTER_URI = "http://localhost:8081/tc";
    private static final String ECSP_ADAPRTER_URI = "http://localhost:{{server.port}}/ecsp/";

    public static List<String> getTrustCircle() throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        logger.info("Get from: " + TC_ADAPRTER_URI);
        HttpEntity<Csp> request = new HttpEntity<Csp>(new Csp("localhost"));
        TrustCircle tc = restTemplate.postForObject(TC_ADAPRTER_URI, request, TrustCircle.class);
        logger.info(tc.toString());
        return tc.getCsps();
    }

}
