package com.intrasoft.csp.client.impl;

import com.intrasoft.csp.anon.model.IntegrationAnonData;
import com.intrasoft.csp.client.AnonClient;
import com.intrasoft.csp.client.impl.TrustCirclesClientImpl;
import com.intrasoft.csp.commons.client.ApiVersionClient;
import com.intrasoft.csp.commons.client.RetryRestTemplate;
//import com.intrasoft.csp.commons.model.IntegrationAnonData;
import com.intrasoft.csp.commons.exceptions.InvalidDataTypeException;
import com.intrasoft.csp.commons.model.IntegrationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * Created by chris on 14/7/2017.
 */
public class AnonClientImpl implements AnonClient {
    private Logger LOG = (Logger) LoggerFactory.getLogger(TrustCirclesClientImpl.class);

    String context;

    @Autowired
    ApiVersionClient apiVersionClient;

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
    public ResponseEntity<String> postAnonData(IntegrationData integrationData, String context) throws InvalidDataTypeException {
        final String url = apiVersionClient.getApiUrl() + context;
        LOG.debug("API call [post]: " + url);
        ResponseEntity<String> response = retryRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<Object>(integrationData), String.class);
        return response;
    }

}
