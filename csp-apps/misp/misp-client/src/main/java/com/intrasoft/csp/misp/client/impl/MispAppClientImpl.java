package com.intrasoft.csp.misp.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import com.intrasoft.csp.misp.commons.models.generated.SharingGroup;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.List;



public class MispAppClientImpl implements MispAppClient, MispContextUrl {

    String context;
    HttpHeaders headers;
    private Logger LOG = (Logger) LoggerFactory.getLogger(MispAppClientImpl.class);

    @Autowired
    @Qualifier("MispAppRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Autowired
    ObjectMapper objectMapper;

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

        LOG.debug("API call [get]: " + url);
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Object> response = new ResponseEntity<Object>(HttpStatus.OK);
        response = retryRestTemplate.exchange(url, HttpMethod.GET, request, Object.class);
        return response;
    }

    @Override
    public ResponseEntity<String> addMispEvent(String body) {
        String url = context  + "/" + MISP_EVENTS;

        LOG.debug("API call [post]: " + url);
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

        LOG.debug("API call [put]: " + url);
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
        response = retryRestTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        LOG.error(response.getBody());
        return response;
    }

    @Override
    public ResponseEntity<String> updateMispEvent(String location, String body) {
        String url = context  + "/" + MISP_EVENTS + "/" + location.split("/events/")[1];

        LOG.debug("API call [put]: " + url);
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
        response = retryRestTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        return response;
    }

    @Override
    public ResponseEntity<String> deleteMispEvent(String uuid) {
        String url = context  + "/" + MISP_EVENTS + "/" + uuid;

        LOG.debug("API call [delete]: " + url);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = retryRestTemplate.exchange(url, HttpMethod.DELETE, request, String.class);

       // LOG.debug(response.toString());
        retryRestTemplate.delete(url);

        return response;
    }

    @Override
    public ResponseEntity<String> addMispProposal(String eventId, String body) {
        String url = context  + "/" + MISP_SHADOW_ATTRIBUTES + "/" + "add" + "/" + eventId;
        LOG.debug("API call [post]: " + url);
        HttpEntity<?> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = retryRestTemplate.exchange(url, HttpMethod.POST, request, String.class);
        LOG.debug(response.toString());

        return response;
    }

    @Override
    public ResponseEntity<String> updateMispProposal(String attrId) {
        String url = context  + "/" + MISP_SHADOW_ATTRIBUTES + "/" + "edit" + "/" + attrId;
        LOG.debug("API call [put]: " + url);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = retryRestTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        LOG.debug(response.toString());

        return response;
    }

    @Override
    public ResponseEntity<String> deleteMispProposal(String attrId) {
        String url = context  + "/" + MISP_SHADOW_ATTRIBUTES + "/" + "discard" + "/" + attrId;
        LOG.debug("API call [put]: " + url);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = retryRestTemplate.exchange(url, HttpMethod.POST, request, String.class);
        LOG.debug(response.toString());

        return response;
    }

    @Override
    public ResponseEntity<String> updateMispProposal(String attrId, String body) {
        String url = context  + "/" + MISP_SHADOW_ATTRIBUTES + "/" + "edit" + "/" + attrId;
        LOG.debug("API call [put]: " + url);
        HttpEntity<?> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = retryRestTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        LOG.debug(response.toString());
        return response;
    }

    @Override
    public ResponseEntity<String> getMispAttribute(String uuid) {
        String url = context  + "/" + MISP_ATTRIBUTES + "/" + uuid;

        LOG.debug("API call [get]: " + url);
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response;
        response = retryRestTemplate.exchange(url, HttpMethod.GET, request, String.class);
        return response;
    }

    @Override
    public ResponseEntity<String> postMispAttribute(String uuid) {
        String url = context  + "/" + MISP_ATTRIBUTES + "/" + uuid;

        LOG.debug("API call [get]: " + url);
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response;
        response = retryRestTemplate.exchange(url, HttpMethod.POST, request, String.class);
        return response;
    }

    /*
    * Organisations API management
    * */
    @Override
    public OrganisationDTO getMispOrganisation(String uuid) {

        String url = context  + "/" + MISP_ORGANISATIONS_VIEW + "/" + uuid;
        LOG.debug("API call [GET]: " + url);
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
//      TODO: Which filter to use here? Local or both local and external? Create separate get methods for each filter?
        String url = context  + "/" + MISP_ORGANISATIONS_VIEW_ALL_LOCAL_AND_EXTERNAL;
        LOG.debug("API call [GET]: " + url);
        HttpEntity<OrganisationWrapper> request = new HttpEntity<>(headers);

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
        LOG.debug("API call [POST]: " + url);

        OrganisationWrapper tempWrapper = new OrganisationWrapper();
        tempWrapper.setOrganisationDTO(organisationDTO);

        String jsonOrganisationWrapper = null;
        try {
            jsonOrganisationWrapper = objectMapper.writeValueAsString(tempWrapper);
        } catch (JsonProcessingException e) {
            LOG.error("Parse error while adding misp org",e);
        }
        LOG.trace("Adding organisation: "+jsonOrganisationWrapper);

        HttpEntity<OrganisationWrapper> request = new HttpEntity<>(tempWrapper, headers);

        ResponseEntity<OrganisationWrapper> organisationWrapper = new ResponseEntity<>(HttpStatus.OK);

        organisationWrapper = retryRestTemplate.exchange(url, HttpMethod.POST, request, OrganisationWrapper.class);
        LOG.debug(organisationWrapper.getStatusCode().toString() + " " + organisationWrapper.getStatusCode().getReasonPhrase());
        return organisationWrapper.getBody().getOrganisation();

    }

//  TODO: MISP's REST API for editing Organisations is partially working. Issue reported; waiting for feedback.
//  Although we're getting an OK response, the only modifiable field is "name". Editing other fields in MISPS' UI
//  works fine.
    @Override
    public OrganisationDTO updateMispOrganisation(OrganisationDTO organisationDTO) {

        String url = context  + "/" + MISP_ORGANISATIONS_EDIT + "/" + organisationDTO.getId();
        LOG.debug("API call [POST]: " + url);

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
        LOG.debug("API call [POST]: " + url);

        HttpEntity<OrganisationWrapper> request = new HttpEntity<>(headers);

        ResponseEntity<OrganisationWrapper> organisationWrapper;
        try {
            organisationWrapper = retryRestTemplate.exchange(url, HttpMethod.POST, request, OrganisationWrapper.class);

        } catch (Exception e) {
            LOG.error(e.getMessage());
            return false;
        }
        LOG.debug(organisationWrapper.getStatusCode().getReasonPhrase());

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
        LOG.debug("API call [GET]: " + url);
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
        for (Response ri : response.getBody().getResponse()) {
            tempSg = ri.getSharingGroup();
            tempSg.setOrganisation(ri.getOrganisation());
            tempSg.setEditable(ri.isEditable());
            tempSg.setSharingGroupOrg(ri.getSharingGroupOrg());
            tempSg.setSharingGroupServer(ri.getSharingGroupServer());
            sgList.add(tempSg);
        }

        LOG.debug(response.getStatusCode().toString());
        return sgList;
    }

    @Override
    public SharingGroup getMispSharingGroup(String uuid) {

        String url = context  + "/" + MISP_SHARINGGROUPS_VIEW + "/" + uuid;

        LOG.debug("API call [GET]: " + url);
        HttpEntity<Response> request = new HttpEntity<>(headers);

        ResponseEntity<Response> response;
        try {
            response = retryRestTemplate.exchange(url, HttpMethod.GET, request, Response.class);

        } catch (Exception e) {
            LOG.error(e.getMessage());
            return null;
        }

//      TODO: avoid this manual mapping
        SharingGroup sharingGroup = response.getBody().getSharingGroup();
        sharingGroup.setOrganisation(response.getBody().getOrganisation());
        sharingGroup.setSharingGroupOrg(response.getBody().getSharingGroupOrg());
        sharingGroup.setSharingGroupServer(response.getBody().getSharingGroupServer());
        return sharingGroup;

    }

    @Override
    public SharingGroup addMispSharingGroup(SharingGroup sharingGroup) {
        String url = context  + "/" + MISP_SHARINGGROUPS_ADD;

        LOG.debug("API call [POST]: " + url);

        HttpEntity<SharingGroup> request = new HttpEntity<>(sharingGroup, headers);
        ResponseEntity<List<Response>> response;
        try {
            response = retryRestTemplate.exchange(url, HttpMethod.POST, request,
                    new ParameterizedTypeReference<List<Response>>() {
                    });

        } catch (Exception e) {
            LOG.error(e.getMessage());
            return null;
        }
        LOG.debug(response.getStatusCode().toString());

//      TODO: Is the Sharing Groups API supposed to respond with an array when adding a Sharing Group? Investigate.
//      SharingGroupOrg is not embedded in the response's Sharing Group object; mapping it manually
        SharingGroup returnedSg = response.getBody().get(0).getSharingGroup();
        returnedSg.setSharingGroupOrg(response.getBody().get(0).getSharingGroupOrg());
        return returnedSg;
    }

    @Override
    public SharingGroup updateMispSharingGroup(SharingGroup sharingGroup) {

        // Just like MISP's Organisations REST API, SharingGroups API also uses id and not uuid in the url.
        String url = context  + "/" + MISP_SHARINGGROUPS_EDIT + "/" + sharingGroup.getId();

        LOG.debug("API call [POST]: " + url);
        HttpEntity<SharingGroup> request = new HttpEntity<>(sharingGroup, headers);

        ResponseEntity<List<Response>> response;
        try {
            response = retryRestTemplate.exchange(url, HttpMethod.POST, request,
                    new ParameterizedTypeReference<List<Response>>() {
                    });
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return null;
        }

//      TODO: Temporary fix (fix all SG update methods); the sharing group should have the organisations
        response.getBody().get(0).getSharingGroup().setSharingGroupOrg(response.getBody().get(0).getSharingGroupOrg());
        return response.getBody().get(0).getSharingGroup();
    }

    @Override
    public boolean updateMispSharingGroupAddOrganisation(String sharingGroupUuid, String organisationUuid) {
        String url = context + "/" + MISP_SHARINGGROUPS_ADD_ORGANISATION + "/" + sharingGroupUuid + "/" + organisationUuid;
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<String> response;  // = new ResponseEntity<String>(HttpStatus.OK);

        LOG.debug("API call [POST]: " + url);
        try {
            response = retryRestTemplate.exchange(url, HttpMethod.POST, request, String.class);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return false;
        }
        LOG.debug("Organisation("+ organisationUuid + ") is added to Sharing Group(" + sharingGroupUuid+")");
        return true;
    }

    @Override
    public boolean updateMispSharingGroupRemoveOrganisation(String sharingGroupUuid, String organisationUuid) {
        String url = context + "/" + MISP_SHARINGGROUPS_REMOVE_ORGANISATION + "/" + sharingGroupUuid + "/" + organisationUuid;
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<String> response;
        LOG.debug("API call [POST]: " + url);
        try {
            response = retryRestTemplate.exchange(url, HttpMethod.POST, request, String.class);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return false;
        }
        LOG.debug("Organisation("+ organisationUuid + ") is removed from Sharing Group(" + sharingGroupUuid+")");
        return true;
    }

    @Override
    public Boolean deleteMispSharingGroup(String id) {
        String url = context  + "/" + MISP_SHARINGGROUPS_DELETE + "/" + id;

        LOG.debug("API call [POST]: " + url);
        HttpEntity<Response> request = new HttpEntity<>(headers);

        ResponseEntity<Response> response;
        try {
            response = retryRestTemplate.exchange(url, HttpMethod.POST, request, Response.class);

        } catch (Exception e) {
            LOG.error(e.getMessage());
            return false;
        }

        LOG.debug(response.getStatusCode() + " " + response.getStatusCode().getReasonPhrase());
        return true;
    }
}
