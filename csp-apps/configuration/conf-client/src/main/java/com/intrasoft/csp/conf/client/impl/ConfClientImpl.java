package com.intrasoft.csp.conf.client.impl;


import com.intrasoft.csp.conf.client.ConfClient;
import com.intrasoft.csp.conf.commons.context.ApiContextUrl;
import com.intrasoft.csp.conf.commons.model.api.AppInfoDTO;
import com.intrasoft.csp.conf.commons.model.api.RegistrationDTO;
import com.intrasoft.csp.conf.commons.model.api.ResponseDTO;
import com.intrasoft.csp.conf.commons.model.api.UpdateInformationDTO;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.libraries.versioning.client.ApiVersionClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

@Service
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

        try {
            URLConnection urlConnection =
                    new URL(url).openConnection();
            urlConnection.setConnectTimeout(60000);
            urlConnection.setReadTimeout(90000);
            urlConnection.setUseCaches(false);
            urlConnection.connect();
            int code = 200;
            if (urlConnection instanceof HttpURLConnection) {
                code =( (HttpURLConnection) urlConnection).getResponseCode();
            }
            return new ResponseEntity<>(new InputStreamResource(urlConnection.getInputStream()),
                    HttpStatus.valueOf(code));
        } catch (MalformedURLException e) {
            LOG.error("URL created is not correct, error : {}",e.getMessage(),e);
            return new ResponseEntity<Resource>(HttpStatus.NO_CONTENT);
        } catch (IOException e) {
            LOG.error("Download error occured, error : {}",e.getMessage(),e);
            return new ResponseEntity<Resource>(HttpStatus.NO_CONTENT);
        }
    }

    @Override
    public ResponseDTO appInfo(String cspId, AppInfoDTO appInfo) {
        final String url = apiVersionClient.getApiUrl() + API_APPINFO + "/" + cspId;
        LOG.debug("Configuration call [POST]: " + url);
        synchronized (retryRestTemplate) {
            ResponseDTO response = retryRestTemplate.postForObject(url, appInfo, ResponseDTO.class);
            return response;
        }
    }
}
