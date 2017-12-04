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
import com.intrasoft.csp.misp.commons.models.Organisation;
import com.intrasoft.csp.misp.commons.models.OrganisationDTO;
import com.intrasoft.csp.misp.commons.models.OrganisationWrapper;
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
    public ResponseEntity<String> getMispEvent(String uuid) {
        String url = context  + "/" + MISP_EVENTS + "/" + uuid;

        LOG.info("API call [post]: " + url);
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
        response = retryRestTemplate.exchange(url, HttpMethod.GET, request, String.class);
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

    @Override
    public OrganisationDTO getMispOrganisation(String uuid) {

        String url = context  + "/" + MISP_ORGANISATIONS_VIEW + "/" + uuid;

        LOG.info("API call [GET]: " + url);
        HttpEntity<OrganisationWrapper> request = new HttpEntity<>(headers);

//        response = retryRestTemplate.exchange(url, HttpMethod.GET, request, String.class);
        ResponseEntity<OrganisationWrapper> organisationWrapper = retryRestTemplate.exchange(url, HttpMethod.GET, request, OrganisationWrapper.class);



//        OrganisationWrapper organisationWrapper = retryRestTemplate.getForObject(url,  OrganisationWrapper.class);

        return organisationWrapper.getBody().getOrganisation();
    }

    @Override
    public ResponseEntity<String> addMispOrganisation(String body) {
        String url = context  + "/" + MISP_ORGANISATIONS_ADD;

        LOG.info("API call [POST]: " + url);
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
        response = retryRestTemplate.exchange(url, HttpMethod.POST, request, String.class);
        return response;
    }

//    TODO: Investigate why MISP's REST API for editing Organisations doesn't seem to work properly.
//    Even though we're getting an OK response, the only modifiable field is "name".

    @Override
    public ResponseEntity<String> updateMispOrganisation(String body) {
        return null;
    }

    @Override
    public ResponseEntity<String> updateMispOrganisation(String uuid, String object) {
        return null;
    }

    @Override
    public ResponseEntity<String> deleteMispOrganisation(String id) {
        String url = context  + "/" + MISP_ORGANISATIONS_DELETE + "/" + id;

        LOG.info("API call [POST]: " + url);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = retryRestTemplate.exchange(url, HttpMethod.POST, request, String.class);

        LOG.info(response.toString());
        retryRestTemplate.delete(url);

        return response;

    }


}
