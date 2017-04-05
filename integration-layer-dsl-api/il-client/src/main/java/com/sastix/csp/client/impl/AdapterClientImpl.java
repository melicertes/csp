package com.sastix.csp.client.impl;

import com.sastix.csp.client.AdapterClient;
import com.sastix.csp.commons.client.RetryRestTemplate;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.routes.ContextUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * Created by iskitsas on 4/4/17.
 */
public class AdapterClientImpl implements AdapterClient {
    private Logger LOG = (Logger) LoggerFactory.getLogger(AdapterClientImpl.class);
    @Autowired
    @Qualifier("AdapterRestTemplate")
    RetryRestTemplate retryRestTemplate;
    @Override
    public ResponseEntity<String> processNewIntegrationData(IntegrationData integrationData) {
        String url = ContextUrl.ADAPTER_INTEGRATION_DATA;
        LOG.debug("API call [post]: " + url);
        ResponseEntity<String> response = retryRestTemplate.postForEntity(url, integrationData, String.class);
        return response;
    }

    @Override
    public ResponseEntity<String> updateIntegrationData(IntegrationData integrationData) {
        String url = ContextUrl.ADAPTER_INTEGRATION_DATA;
        LOG.debug("API call [put]: " + url);
        ResponseEntity<String> response = retryRestTemplate.exchange(url, HttpMethod.PUT,new HttpEntity<Object>(integrationData), String.class);
        LOG.debug("status code: "+response.getStatusCode());
        return response;
    }

    @Override
    public ResponseEntity<String> deleteIntegrationData(IntegrationData integrationData) {
        String url = ContextUrl.ADAPTER_INTEGRATION_DATA;
        LOG.debug("API call [delete]: " + url);
        ResponseEntity<String> response = retryRestTemplate.exchange(url, HttpMethod.DELETE,new HttpEntity<Object>(integrationData), String.class);
        LOG.debug("status code: "+response.getStatusCode());
        return response;
    }
}
