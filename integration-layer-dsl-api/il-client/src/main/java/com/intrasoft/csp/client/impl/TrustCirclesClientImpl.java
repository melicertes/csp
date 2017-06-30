package com.intrasoft.csp.client.impl;

import com.intrasoft.csp.client.TrustCirclesClient;
import com.intrasoft.csp.commons.client.RetryRestTemplate;
import com.intrasoft.csp.commons.model.TrustCircle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by iskitsas on 4/8/17.
 */
public class TrustCirclesClientImpl implements TrustCirclesClient {
    String context;
    private Logger LOG = (Logger) LoggerFactory.getLogger(TrustCirclesClientImpl.class);
    @Autowired
    @Qualifier("CspRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Value("${tc.path.circles}")
    String trustCircle;

    @Override
    public void setProtocolHostPort(String protocol, String host, String port) {
        context = protocol+"://"+host+":"+port;
    }

    @Override
    public TrustCircle getTrustCircle(Integer id) {
        String url = context + trustCircle + "/"+id;
        LOG.debug("API call [post]: " + url);
        TrustCircle trustCircle = retryRestTemplate.getForObject(url, TrustCircle.class);
        return trustCircle;
    }

    @Override
    public String getContext() {
        return context;
    }
}
