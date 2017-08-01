package com.intrasoft.csp.anon.client.impl;

import com.intrasoft.csp.anon.client.AnonClient;
import com.intrasoft.csp.anon.commons.exceptions.InvalidDataTypeException;
import com.intrasoft.csp.anon.commons.model.AnonContextUrl;

import com.intrasoft.csp.anon.commons.model.IntegrationAnonData;

import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.libraries.versioning.client.ApiVersionClient;
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
public class AnonClientImpl implements AnonClient, AnonContextUrl {
    private Logger LOG = (Logger) LoggerFactory.getLogger(AnonClientImpl.class);

    @Autowired
    ApiVersionClient apiVersionClient;


    @Autowired
    @Qualifier("AnonRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Override
    public ResponseEntity<String> postAnonData(IntegrationAnonData integrationAnonData, String context) throws InvalidDataTypeException {
        final String url = apiVersionClient.getApiUrl() + context;
        LOG.debug("ANON call [post]: " + url);
        ResponseEntity<String> response = retryRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<Object>(integrationAnonData), String.class);
        return response;
    }

}
