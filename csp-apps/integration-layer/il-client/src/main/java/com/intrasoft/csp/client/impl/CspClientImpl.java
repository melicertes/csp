package com.intrasoft.csp.client.impl;

import com.intrasoft.csp.client.CspClient;
import com.intrasoft.csp.commons.exceptions.InvalidDataTypeException;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.routes.ContextUrl;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.libraries.versioning.client.ApiVersionClient;
import com.intrasoft.csp.libraries.versioning.model.VersionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * Created by iskitsas on 5/3/17.
 */
public class CspClientImpl implements CspClient, ContextUrl {
    private Logger LOG = (Logger) LoggerFactory.getLogger(CspClientImpl.class);

    @Autowired
    @Qualifier("CspApiVersionClient")
    ApiVersionClient apiVersionClient;


    @Autowired
    @Qualifier("CspRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Override
    public VersionDTO getApiVersion() {
        return apiVersionClient.getApiVersion();
    }

    @Override
    public ResponseEntity<String> postIntegrationData(IntegrationData integrationData, String context) throws InvalidDataTypeException{
        final String url = apiVersionClient.getApiUrl() + context;
        LOG.debug("API call [post]: " + url);
        ResponseEntity<String> response = retryRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<Object>(integrationData), String.class);
        return response;
    }

    @Override
    public ResponseEntity<String> updateIntegrationData(IntegrationData integrationData, String context) {
        final String url = apiVersionClient.getApiUrl() + context;
        LOG.debug("API call [put]: " + url);
        ResponseEntity<String> response = retryRestTemplate.exchange(url, HttpMethod.PUT,new HttpEntity<Object>(integrationData), String.class);
        LOG.debug("status code: "+response.getStatusCode());
        return response;
    }

    @Override
    public ResponseEntity<String> deleteIntegrationData(IntegrationData integrationData, String context) {
        final String url = apiVersionClient.getApiUrl() + context;
        LOG.debug("API call [delete]: " + url);
        ResponseEntity<String> response = retryRestTemplate.exchange(url, HttpMethod.DELETE,new HttpEntity<Object>(integrationData), String.class);
        LOG.debug("status code: "+response.getStatusCode());
        return response;
    }

}
