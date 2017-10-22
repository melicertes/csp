package com.intrasoft.csp.misp.client.impl;

//import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intrasoft.csp.misp.client.MispAppClient;
import com.intrasoft.csp.misp.commons.config.MispContextUrl;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

//    @Autowired
//    @Qualifier("CspRestTemplate")
//    RetryRestTemplate retryRestTemplate;

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
    public ResponseEntity<String> addMispEvent(String object) {
        String url = context  + "/" + MISP_EVENTS;

        LOG.info("API call [post]: " + url);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<>(new String(object.toString()), headers);
//        LOG.info();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        return response;
    }

    @Override
    public ResponseEntity<String> updateMispEvent(String uuid, String object) throws IOException {
        String url = context  + "/" + MISP_EVENTS + "/" + uuid;

        JsonNode jsonNode = new ObjectMapper().readTree(object);
        JsonNode node = jsonNode.path("Event");
        ObjectNode objectNode = (ObjectNode) node;
        objectNode.put("timestamp", String.valueOf(DateTime.now().getMillis()/1000));
//        return object.toString();
        object = jsonNode.toString();

        LOG.info("API call [put]: " + url);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<>(new String(object.toString()), headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        return response;
    }

    @Override
    public ResponseEntity<String> deleteMispEvent(String uuid) {
        String url = context  + "/" + MISP_EVENTS + "/" + uuid;

        LOG.info("API call [delete]: " + url);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);

        LOG.info(response.toString());
        restTemplate.delete(url);

        return response;
    }
}
