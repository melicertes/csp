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
    String context;
    private Logger LOG = (Logger) LoggerFactory.getLogger(AdapterClientImpl.class);
    @Autowired
    @Qualifier("CspRestTemplate")
    RetryRestTemplate retryRestTemplate;
    @Override
    public ResponseEntity<String> processNewIntegrationData(IntegrationData integrationData) {
        String url = context+ContextUrl.ADAPTER_INTEGRATION_DATA;
        LOG.debug("API call [post]: " + url);
        ResponseEntity<String> response = retryRestTemplate.exchange(url,HttpMethod.POST, new HttpEntity<Object>(integrationData), String.class);
        return response;
    }

    @Override
    public ResponseEntity<String> updateIntegrationData(IntegrationData integrationData) {
        String url = context+ContextUrl.ADAPTER_INTEGRATION_DATA;
        LOG.debug("API call [put]: " + url);
        ResponseEntity<String> response = retryRestTemplate.exchange(url, HttpMethod.PUT,new HttpEntity<Object>(integrationData), String.class);
        LOG.debug("status code: "+response.getStatusCode());
        return response;
    }

    @Override
    public ResponseEntity<String> deleteIntegrationData(IntegrationData integrationData) {
        String url = context+ContextUrl.ADAPTER_INTEGRATION_DATA;
        LOG.debug("API call [delete]: " + url);
        ResponseEntity<String> response = retryRestTemplate.exchange(url, HttpMethod.DELETE,new HttpEntity<Object>(integrationData), String.class);
        LOG.debug("status code: "+response.getStatusCode());
        return response;
    }

    @Override
    public void setProtocolHostPort(String protocol, String host, String port) {
        context = protocol+"://"+host+":"+port;
    }

    @Override
    public String getContext() {
        return context;
    }
}
