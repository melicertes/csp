package com.intrasoft.csp.misp.client.impl;

//import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.misp.client.MispAppClient;
import com.intrasoft.csp.misp.commons.config.MispContextUrl;
import com.intrasoft.csp.misp.commons.models.OrganisationDTO;
import com.intrasoft.csp.misp.commons.models.OrganisationWrapper;
import com.intrasoft.csp.misp.commons.models.generated.Response;
import com.intrasoft.csp.misp.commons.models.generated.ResponseAll;
import com.intrasoft.csp.misp.commons.models.generated.ResponseItem;
import com.intrasoft.csp.misp.commons.models.generated.SharingGroup;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.security.interfaces.RSAPrivateCrtKey;
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

    /*
    * Organisations API management
    * */
    @Override
    public OrganisationDTO getMispOrganisation(String uuid) {

        String url = context  + "/" + MISP_ORGANISATIONS_VIEW + "/" + uuid;
        LOG.info("API call [GET]: " + url);
        HttpEntity<OrganisationWrapper> request = new HttpEntity<>(headers);

        ResponseEntity<OrganisationWrapper> organisationWrapper;
        try {
            organisationWrapper = retryRestTemplate.exchange(url, HttpMethod.GET, request, OrganisationWrapper.class);

        } catch (Exception e) {
            LOG.error(e.getMessage());
            return null;
        }

        return organisationWrapper.getBody().getOrganisation();
    }

    @Override
    public List<OrganisationDTO> getAllMispOrganisations() {
//      TODO: Which filter to use here? Local or both local and external?
        String url = context  + "/" + MISP_ORGANISATIONS_VIEW_ALL_LOCAL; // There are external and all options as well.
        LOG.info("API call [GET]: " + url);
        HttpEntity<OrganisationWrapper> request = new HttpEntity<>(headers);

        // JSON response's structure for this call is straightforward and doesn't require a wrapper.
        ResponseEntity<List<OrganisationWrapper>> organisationResponse;
        try {
            organisationResponse = retryRestTemplate.exchange(url, HttpMethod.GET, request,
                    new ParameterizedTypeReference<List<OrganisationWrapper>>() {});
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return null;
        }

        // Unwrap the organisation dtos and finally return them as a list
        List<OrganisationDTO> returnList = new ArrayList<>();
        organisationResponse.getBody().forEach(wrapper -> {
            returnList.add(wrapper.getOrganisation());
        });

        return returnList;
    }

    @Override
    public OrganisationDTO addMispOrganisation(OrganisationDTO organisationDTO) {
        String url = context  + "/" + MISP_ORGANISATIONS_ADD;
        LOG.info("API call [POST]: " + url);

        OrganisationWrapper tempWrapper = new OrganisationWrapper();
        tempWrapper.setOrganisationDTO(organisationDTO);

        HttpEntity<OrganisationWrapper> request = new HttpEntity<>(tempWrapper, headers);

        ResponseEntity<OrganisationWrapper> organisationWrapper = new ResponseEntity<>(HttpStatus.OK);

        organisationWrapper = retryRestTemplate.exchange(url, HttpMethod.POST, request, OrganisationWrapper.class);
        LOG.info(organisationWrapper.getStatusCode().toString() + " " + organisationWrapper.getStatusCode().getReasonPhrase());
        return organisationWrapper.getBody().getOrganisation();

    }

//  TODO: MISP's REST API for editing Organisations is partially working. Issue reported; waiting for feedback.
//  Although we're getting an OK response, the only modifiable field is "name". Editing other fields in MISPS' UI
//  works fine.
    @Override
    public OrganisationDTO updateMispOrganisation(OrganisationDTO organisationDTO) {

        String url = context  + "/" + MISP_ORGANISATIONS_EDIT + "/" + organisationDTO.getId();
        LOG.info("API call [POST]: " + url);

        OrganisationWrapper tempWrapper = new OrganisationWrapper();
        tempWrapper.setOrganisationDTO(organisationDTO);

        HttpEntity<OrganisationWrapper> request = new HttpEntity<>(tempWrapper, headers);

        ResponseEntity<OrganisationWrapper> organisationWrapper = new ResponseEntity<>(HttpStatus.OK);

        organisationWrapper = retryRestTemplate.exchange(url, HttpMethod.POST, request, OrganisationWrapper.class);
        return organisationWrapper.getBody().getOrganisation();

    }

    @Override
    public boolean deleteMispOrganisation(String id) {

        String url = context  + "/" + MISP_ORGANISATIONS_DELETE + "/" + id;
        LOG.info("API call [POST]: " + url);

        HttpEntity<OrganisationWrapper> request = new HttpEntity<>(headers);

        ResponseEntity<OrganisationWrapper> organisationWrapper;
        try {
            organisationWrapper = retryRestTemplate.exchange(url, HttpMethod.POST, request, OrganisationWrapper.class);

        } catch (Exception e) {
            LOG.error(e.getMessage());
            return false;
        }
        LOG.info(organisationWrapper.getStatusCode().getReasonPhrase());

        if (organisationWrapper.getStatusCode().value() == 200)
            return true;


        return false;

    }

    /*
    * Sharing Groups API management
    * */

    @Override
    public List<SharingGroup> getAllMispSharingGroups() {

        String url = context  + "/" + MISP_SHARINGGROUPS_VIEW_ALL;
        LOG.info("API call [GET]: " + url);
        HttpEntity<ResponseAll> request = new HttpEntity<>(headers);

        ResponseEntity<ResponseAll> response;
        try {
            response = retryRestTemplate.exchange(url, HttpMethod.GET, request, ResponseAll.class);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return null;
        }

        // Manually mapping the Sharing Group creator organisation and editable field.
        List<SharingGroup> sgList = new ArrayList<>();
        SharingGroup tempSg = null;
        for (ResponseItem ri : response.getBody().getResponse()) {
            tempSg = ri.getSharingGroup();
            tempSg.setCreatedBy(ri.getOrganisation());
            tempSg.setEditable(ri.isEditable());
            tempSg.setSharingGroupOrg(ri.getSharingGroupOrg());
            tempSg.setSharingGroupServer(ri.getSharingGroupServer());
            sgList.add(tempSg);
        }

        LOG.info(response.getStatusCode().toString());
        return sgList;
    }

    @Override
    public SharingGroup getMispSharingGroup(String uuid) {

        String url = context  + "/" + MISP_SHARINGGROUPS_VIEW + "/" + uuid;

        LOG.info("API call [GET]: " + url);
        HttpEntity<Response> request = new HttpEntity<>(headers);

        ResponseEntity<Response> response;
        try {
            response = retryRestTemplate.exchange(url, HttpMethod.GET, request, Response.class);

        } catch (Exception e) {
            LOG.error(e.getMessage());
            return null;
        }

        return response.getBody().getSharingGroup();

    }

    @Override
    public SharingGroup addMispSharingGroup(SharingGroup sharingGroup) {
        String url = context  + "/" + MISP_SHARINGGROUPS_ADD;

        Response resp = new Response();
        resp.setSharingGroup(sharingGroup);

        LOG.info("API call [POST]: " + url);
        HttpEntity<Response> request = new HttpEntity<>(headers);

        ResponseEntity<Response> response;
        try {
            response = retryRestTemplate.exchange(url, HttpMethod.POST, request, Response.class);

        } catch (Exception e) {
            LOG.error(e.getMessage());
            return null;
        }
        LOG.info(response.getStatusCode().toString());
        return response.getBody().getSharingGroup();
    }

    @Override
    public SharingGroup updateMispSharingGroup(SharingGroup sharingGroup) {

        // Just like MISP's Organisations REST API, we're assuming SharingGroups API also uses id and not uuid in the url.
        String url = context  + "/" + MISP_SHARINGGROUPS_EDIT + "/" + sharingGroup.getId();

        Response resp = new Response();
        resp.setSharingGroup(sharingGroup);

        LOG.info("API call [POST]: " + url);
        HttpEntity<Response> request = new HttpEntity<>(headers);

        ResponseEntity<Response> response;
        try {
            response = retryRestTemplate.exchange(url, HttpMethod.POST, request, Response.class);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return null;
        }

        return response.getBody().getSharingGroup();
    }

    @Override
    public boolean deleteMispSharingGroup(String id) {
        String url = context  + "/" + MISP_SHARINGGROUPS_DELETE + "/" + id;

        LOG.info("API call [POST]: " + url);
        HttpEntity<Response> request = new HttpEntity<>(headers);

        ResponseEntity<Boolean> response;
        try {
            response = retryRestTemplate.exchange(url, HttpMethod.POST, request, Boolean.class);

        } catch (Exception e) {
            LOG.error(e.getMessage());
            return false;
        }

        LOG.error(response.getStatusCode() + " " + response.getStatusCode().getReasonPhrase());
        return true;
    }
}
