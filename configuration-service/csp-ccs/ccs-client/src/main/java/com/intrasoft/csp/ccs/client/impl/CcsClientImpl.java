package com.intrasoft.csp.ccs.client.impl;


import com.intrasoft.csp.ccs.client.CcsClient;
import com.intrasoft.csp.ccs.commons.client.ApiVersionClient;
import com.intrasoft.csp.ccs.commons.client.RetryRestTemplate;
import com.intrasoft.csp.ccs.commons.exceptions.InvalidDataTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class CcsClientImpl implements CcsClient {

    private Logger LOG = (Logger) LoggerFactory.getLogger(CcsClientImpl.class);

    @Autowired
    ApiVersionClient apiVersionClient;


    @Autowired
    @Qualifier("CspRestTemplate")
    RetryRestTemplate retryRestTemplate;



    @Override
    public ResponseEntity getUpdates(String cspId) throws InvalidDataTypeException {
        final String url = apiVersionClient.getApiUrl() + cspId;
        LOG.debug("Ccs call [post]: " + url);
        //ResponseEntity response = retryRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<Object>(integrationData), String.class);
        ResponseEntity response = retryRestTemplate.exchange(url, HttpMethod.POST, null, String.class);
        return response;
    }

}
