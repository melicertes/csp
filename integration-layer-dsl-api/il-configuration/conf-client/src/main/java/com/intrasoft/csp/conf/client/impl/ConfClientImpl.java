package com.intrasoft.csp.conf.client.impl;


import com.intrasoft.csp.commons.exceptions.InvalidDataTypeException;
import com.intrasoft.csp.conf.client.ConfClient;
import com.intrasoft.csp.conf.commons.context.ApiContextUrl;
import com.intrasoft.csp.conf.commons.model.AppInfoDTO;
import com.intrasoft.csp.conf.commons.model.RegistrationDTO;
import com.intrasoft.csp.conf.commons.model.ResponseDTO;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.libraries.versioning.client.ApiVersionClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;


public class ConfClientImpl implements ConfClient, ApiContextUrl {

    private Logger LOG = (Logger) LoggerFactory.getLogger(ConfClientImpl.class);

    @Autowired
    ApiVersionClient apiVersionClient;

    @Autowired
    @Qualifier("ConfRestTemplate")
    RetryRestTemplate retryRestTemplate;


    @Override
    public ResponseEntity updates(String cspId) {
        final String url = apiVersionClient.getApiUrl() + API_BASEURL + API_UPDATES + "/" + cspId;
        LOG.debug("Configuration call [GET]: " + url);

        ResponseEntity response = retryRestTemplate.exchange(url, HttpMethod.GET, null, String.class);
        return response;
    }

    @Override
    public ResponseEntity register(String cspId, RegistrationDTO cspRegistration) {
        final String url = apiVersionClient.getApiUrl() + API_BASEURL + API_REGISTER + "/" + cspId;
        LOG.debug("Configuration call [POST]: " + url);

        /**
         * @TODO
         * At test fails to persist, but api service logs success
         */
        ResponseEntity response = retryRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<Object>(cspRegistration), ResponseDTO.class);
        return response;
    }

    @Override
    public ResponseEntity update(String cspId, String updateHash) {
        final String url = apiVersionClient.getApiUrl() + API_BASEURL + API_UPDATE + "/" + cspId + "/" + updateHash;
        LOG.debug("Configuration call [GET]: " + url);

        /**
         * @TODO
         * Service returns either file stream or ResponseDTO. How to implement?
         */
        ResponseEntity response = retryRestTemplate.exchange(url, HttpMethod.GET, null, Stream.class);
        return response;
    }

    @Override
    public ResponseEntity appInfo(String cspId, AppInfoDTO appInfo) {
        final String url = apiVersionClient.getApiUrl() + API_BASEURL + API_APPINFO + "/" + cspId;
        LOG.debug("Configuration call [POST]: " + url);

        ResponseEntity response = retryRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<Object>(appInfo), String.class);
        return response;
    }
}
