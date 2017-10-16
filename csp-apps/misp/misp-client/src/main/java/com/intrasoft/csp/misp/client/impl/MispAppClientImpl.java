package com.intrasoft.csp.misp.client.impl;

//import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.misp.client.MispAppClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class MispAppClientImpl implements MispAppClient{

    String context;
    HttpHeaders headers;
    private Logger LOG = (Logger) LoggerFactory.getLogger(MispAppClientImpl.class);

//    @Autowired
//    @Qualifier("CspRestTemplate")
//    RetryRestTemplate retryRestTemplate;

    @Value("${misp.app.events.path}")
    String eVentsPath;


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
        String url = context  + "/" + eVentsPath;

        LOG.info("API call [post]: " + url);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<>(new String(object.toString()), headers);
        ResponseEntity<String> response = restTemplate.exchange(context, HttpMethod.POST, request, String.class);
        return response;
    }

    @Override
    public ResponseEntity<String> updateMispEvent(String object) {
        String url = context  + "/" + eVentsPath;

        LOG.info("API call [post]: " + url);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<>(new String(object.toString()), headers);
        ResponseEntity<String> response = restTemplate.exchange(context, HttpMethod.PUT, request, String.class);
        return response;
    }
}
