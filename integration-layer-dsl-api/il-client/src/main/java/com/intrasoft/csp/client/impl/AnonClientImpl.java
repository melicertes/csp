package com.intrasoft.csp.client.impl;

import com.intrasoft.csp.client.AnonClient;
import com.intrasoft.csp.client.impl.TrustCirclesClientImpl;
import com.intrasoft.csp.commons.client.RetryRestTemplate;
import com.intrasoft.csp.commons.model.IntegrationAnonData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created by chris on 14/7/2017.
 */
public class AnonClientImpl implements AnonClient {
    private Logger LOG = (Logger) LoggerFactory.getLogger(TrustCirclesClientImpl.class);

    String context;

    @Autowired
    @Qualifier("CspRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Override
    public void setProtocolHostPort(String protocol, String host, String port) {
        context = protocol+"://"+host+":"+port;
    }

    @Override
    public String getContext() {
        return context;
    }

    @Override
    public IntegrationAnonData getAnonData(Object object) {
        String url = context;
        LOG.debug("API call [post]: " + url);
        IntegrationAnonData integrationAnonData = retryRestTemplate.postForObject(context, object,IntegrationAnonData.class);
        return integrationAnonData;
    }
}
