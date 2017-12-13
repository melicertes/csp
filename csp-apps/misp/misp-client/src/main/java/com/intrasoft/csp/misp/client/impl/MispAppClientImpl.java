package com.intrasoft.csp.misp.client.impl;

//import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;
import com.intrasoft.csp.libraries.restclient.handlers.CommonExceptionHandler;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.misp.client.MispAppClient;
import com.intrasoft.csp.misp.commons.config.MispContextUrl;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @TODO change {@link RestTemplate} to {@link com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate}
 */


public class MispAppClientImpl implements MispAppClient, MispContextUrl {

    String context;
    HttpHeaders headers;
    private Logger LOG = (Logger) LoggerFactory.getLogger(MispAppClientImpl.class);

    @Autowired
    @Qualifier("MispAppRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Override
    public void setProtocolHostPortHeaders(String protocol, String host, String port, String authorizationKey) {
        context = protocol+"://"+host+":"+port;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List accepts = new ArrayList();
        accepts.add(MediaType.APPLICATION_JSON);
        headers.setAccept(accepts);
        headers.set("Authorization", authorizationKey);

    }

    @Override
    public String getContext() {
        return context;
    }

    @Override
    public ResponseEntity<Object> getMispEvent(String uuid) {
        String url = context  + "/" + MISP_EVENTS + "/" + uuid;

        LOG.info("API call [get]: " + url);
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Object> response = new ResponseEntity<Object>(HttpStatus.OK);
        response = retryRestTemplate.exchange(url, HttpMethod.GET, request, Object.class);
        return response;
    }

    @Override
    public ResponseEntity<String> addMispEvent(String body) {
        String url = context  + "/" + MISP_EVENTS;

        LOG.info("API call [post]: " + url);
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
        response = retryRestTemplate.exchange(url, HttpMethod.POST, request, String.class);
        return response;
    }

    @Override
    public ResponseEntity<String> updateMispEvent(String body) {
        String url = context  + "/" + MISP_EVENTS;

        JsonNode jsonNode = new ObjectMapper().convertValue(body, JsonNode.class);
        JsonNode node = jsonNode.path("Event");
        ObjectNode objectNode = (ObjectNode) node;
        objectNode.put("timestamp", String.valueOf(DateTime.now().getMillis()/1000));
        body = jsonNode.toString();

        LOG.info("API call [put]: " + url);
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
        response = retryRestTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        LOG.error(response.getBody());
        return response;
    }

    @Override
    public ResponseEntity<String> updateMispEvent(String location, String body) {
        String url = context  + "/" + MISP_EVENTS + "/" + location.split("/events/")[1];

        LOG.info("API call [put]: " + url);
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
        response = retryRestTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        return response;
    }

    @Override
    public ResponseEntity<String> deleteMispEvent(String uuid) {
        String url = context  + "/" + MISP_EVENTS + "/" + uuid;

        LOG.info("API call [delete]: " + url);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = retryRestTemplate.exchange(url, HttpMethod.DELETE, request, String.class);

        LOG.info(response.toString());
        retryRestTemplate.delete(url);

        return response;
    }
}
