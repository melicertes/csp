package com.intrasoft.csp.conf.client.impl;


import com.intrasoft.csp.conf.client.ConfClient;
import com.intrasoft.csp.conf.commons.context.ApiContextUrl;
import com.intrasoft.csp.conf.commons.model.AppInfoDTO;
import com.intrasoft.csp.conf.commons.model.RegistrationDTO;
import com.intrasoft.csp.conf.commons.model.ResponseDTO;
import com.intrasoft.csp.conf.commons.model.UpdateInformationDTO;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.libraries.versioning.client.ApiVersionClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.stream.Stream;


public class ConfClientImpl implements ConfClient, ApiContextUrl {

    private Logger LOG = (Logger) LoggerFactory.getLogger(ConfClientImpl.class);

    @Autowired
    @Qualifier("ConfApiVersionClient")
    ApiVersionClient apiVersionClient;

    @Autowired
    @Qualifier("ConfRestTemplate")
    RetryRestTemplate retryRestTemplate;


    @Override
    public UpdateInformationDTO updates(String cspId) {
        final String url = apiVersionClient.getApiUrl() + API_UPDATES + "/" + cspId;
        LOG.debug("Configuration call [GET]: " + url);
        UpdateInformationDTO response = retryRestTemplate.getForObject(url, UpdateInformationDTO.class);
        return response;
    }

    @Override
    public ResponseDTO register(String cspId, RegistrationDTO cspRegistration) {
        final String url = apiVersionClient.getApiUrl() + API_REGISTER + "/" + cspId;
        LOG.debug("Configuration call [POST]: " + url);
        ResponseDTO responseDTO = retryRestTemplate.postForObject(url, cspRegistration, ResponseDTO.class);
        return responseDTO;
    }

    @Override
    public ResponseEntity update(String cspId, String updateHash) {
        final String url = apiVersionClient.getApiUrl() + API_UPDATE + "/" + cspId + "/" + updateHash;
        LOG.debug("Configuration call [GET]: " + url);
        ResponseEntity response = retryRestTemplate.exchange(url, HttpMethod.GET, null, Resource.class);
        return response;
    }

    @Override
    public ResponseDTO appInfo(String cspId, AppInfoDTO appInfo) {
        final String url = apiVersionClient.getApiUrl() + API_APPINFO + "/" + cspId;
        LOG.debug("Configuration call [POST]: " + url);
        ResponseDTO response = retryRestTemplate.postForObject(url, appInfo, ResponseDTO.class);//retryRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<Object>(appInfo), ResponseDTO.class);
        return response;
    }
}
