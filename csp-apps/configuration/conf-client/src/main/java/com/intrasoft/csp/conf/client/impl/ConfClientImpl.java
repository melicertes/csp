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
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseExtractor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        synchronized (retryRestTemplate) { //making sure only we use the template now

            UpdateInformationDTO response = retryRestTemplate.getForObject(url, UpdateInformationDTO.class);
            return response;

        }
    }

    @Override
    public ResponseDTO register(String cspId, RegistrationDTO cspRegistration) {
        final String url = apiVersionClient.getApiUrl() + API_REGISTER + "/" + cspId;
        LOG.debug("Configuration call [POST]: " + url);
        synchronized (retryRestTemplate) { //making sure only we use the template now

            ResponseDTO responseDTO = retryRestTemplate.postForObject(url, cspRegistration, ResponseDTO.class);
            return responseDTO;
        }
    }



    @Override
    public ResponseEntity update(String cspId, String updateHash) {
        final String url = apiVersionClient.getApiUrl() + API_UPDATE + "/" + cspId + "/" + updateHash;
        LOG.debug("Configuration call [GET]: " + url);

        SimpleClientHttpRequestFactory unbufferedFactory = new SimpleClientHttpRequestFactory();
        unbufferedFactory.setBufferRequestBody(false);
        ClientHttpRequestFactory orig = retryRestTemplate.getRequestFactory();

        try {
            synchronized (retryRestTemplate) { //making sure only we use the template now
                retryRestTemplate.setRequestFactory(unbufferedFactory);
                ResponseEntity response = retryRestTemplate.execute(new URI(url), HttpMethod.GET, null, (ResponseExtractor<ResponseEntity<Resource>>) clientHttpResponse -> {
                    if (clientHttpResponse.getStatusCode() == HttpStatus.OK) {
                        Path tmpLocation = new File(System.getProperty("java.io.tmpdir"),"download"+System.currentTimeMillis()+".tmp").toPath();
                        Files.copy( clientHttpResponse.getBody(), tmpLocation);
                        return new ResponseEntity<Resource>(new FileSystemResource(tmpLocation.toFile()),clientHttpResponse.getStatusCode());
                    } else {
                        return new ResponseEntity<Resource>(clientHttpResponse.getStatusCode());
                    }
                });
                return response;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return new ResponseEntity<Resource>(HttpStatus.NO_CONTENT);
        } finally {
            retryRestTemplate.setRequestFactory(orig);
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
