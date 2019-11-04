package com.intrasoft.csp.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.client.CspClient;
import com.intrasoft.csp.commons.exceptions.InvalidDataTypeException;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.routes.ContextUrl;
import com.intrasoft.csp.commons.validators.HmacHelper;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.libraries.versioning.client.ApiVersionClient;
import com.intrasoft.csp.libraries.versioning.model.VersionDTO;
import net.openhft.hashing.LongHashFunction;
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
    ObjectMapper mapper;

    @Autowired
    @Qualifier("CspRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Override
    public VersionDTO getApiVersion() {
        return apiVersionClient.getApiVersion();
    }

    @Override
    public ResponseEntity<String> postIntegrationData(IntegrationData integrationData) throws InvalidDataTypeException{
        final String url = apiVersionClient.getApiUrl() + DSL_INTEGRATION_DATA;
        HmacHelper.getInstance().hmacIntegrationData(integrationData);
        LOG.debug("API call [post]: " + url);
        ResponseEntity<String> response = retryRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<Object>(integrationData), String.class);
        LOG.debug("API call [post] status code: "+response.getStatusCode());
        return response;
    }

    @Override
    public ResponseEntity<String> updateIntegrationData(IntegrationData integrationData) {
        final String url = apiVersionClient.getApiUrl() + DSL_INTEGRATION_DATA;
        HmacHelper.getInstance().hmacIntegrationData(integrationData);
        LOG.debug("API call [put]: " + url);
        ResponseEntity<String> response = retryRestTemplate.exchange(url, HttpMethod.PUT,new HttpEntity<Object>(integrationData), String.class);
        LOG.debug("API call [put] status code: "+response.getStatusCode());
        return response;
    }

    @Override
    public ResponseEntity<String> deleteIntegrationData(IntegrationData integrationData) {
        final String url = apiVersionClient.getApiUrl() + DSL_INTEGRATION_DATA;
        HmacHelper.getInstance().hmacIntegrationData(integrationData);
        LOG.debug("API call [delete]: " + url);
        ResponseEntity<String> response = retryRestTemplate.exchange(url, HttpMethod.DELETE,new HttpEntity<Object>(integrationData), String.class);
        LOG.debug("API call [delete] status code: "+response.getStatusCode());
        return response;
    }




}
