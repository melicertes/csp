package com.sastix.csp.client.impl;

import com.sastix.csp.client.TrustCirclesClient;
import com.sastix.csp.commons.client.RetryRestTemplate;
import com.sastix.csp.commons.model.Csp;
import com.sastix.csp.commons.model.TrustCircle;
import com.sastix.csp.commons.routes.ContextUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * Created by iskitsas on 4/8/17.
 */
public class TrustCirclesClientImpl implements TrustCirclesClient {
    String context;
    private Logger LOG = (Logger) LoggerFactory.getLogger(TrustCirclesClientImpl.class);
    @Autowired
    @Qualifier("CspRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Override
    public void setProtocolHostPort(String protocol, String host, String port) {
        context = protocol+"://"+host+":"+port;
    }

    @Override
    public List<String> getCsps(String id) {
        String url = context+ ContextUrl.TRUST_CIRCLE;
        LOG.debug("API call [post]: " + url);
        TrustCircle trustCircle = retryRestTemplate.postForObject(url, new Csp(id), TrustCircle.class);
        return trustCircle.getCsps();
    }

    @Override
    public String getContext() {
        return context;
    }
}
