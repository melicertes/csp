package com.intrasoft.csp.misp.client.impl;

import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.libraries.versioning.client.ApiVersionClient;
import com.intrasoft.csp.misp.client.MispClient;
import com.intrasoft.csp.misp.commons.config.ApiContextUrl;
import com.intrasoft.csp.misp.commons.config.MispContextUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class MispClientImpl implements MispClient, ApiContextUrl, MispContextUrl {

    private Logger LOG = (Logger) LoggerFactory.getLogger(MispClientImpl.class);

    @Autowired
    @Qualifier("MispApiVersionClient")
    ApiVersionClient apiVersionClient;

    @Autowired
    @Qualifier("MispRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Override
    public IntegrationData postIntegrationDataAdapter(IntegrationData integrationData) {
        final String url = apiVersionClient.getApiUrl() + API_ADAPTER;
        LOG.info("MISP-ADAPTER call [post]: " + url);
        IntegrationData response = retryRestTemplate.postForObject(url, integrationData, IntegrationData.class);
        return response;
    }

    @Override
    public IntegrationData postIntegrationDataEmitter(IntegrationData integrationData) {
        final String url = apiVersionClient.getApiUrl() + API_EMITTER;
        LOG.info("MISP-ADAPTER call [post]: " + url);
        IntegrationData response = retryRestTemplate.postForObject(url, integrationData, IntegrationData.class);
        return response;
    }
}
