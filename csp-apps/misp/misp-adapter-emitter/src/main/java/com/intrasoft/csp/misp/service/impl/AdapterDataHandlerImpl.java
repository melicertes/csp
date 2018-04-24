package com.intrasoft.csp.misp.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intrasoft.csp.client.ElasticClient;
import com.intrasoft.csp.commons.model.DataParams;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.libraries.restclient.exceptions.StatusCodeException;
import com.intrasoft.csp.misp.client.MispAppClient;
import com.intrasoft.csp.misp.commons.config.MispContextUrl;
import com.intrasoft.csp.misp.commons.models.ShadowAttributeRequestDTO;
import com.intrasoft.csp.misp.domain.model.Origin;
import com.intrasoft.csp.misp.domain.service.impl.OriginServiceImpl;
import com.intrasoft.csp.misp.service.AdapterDataHandler;
import com.intrasoft.csp.misp.service.EmitterDataHandler;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.intrasoft.csp.misp.commons.config.MispContextUrl.MispEntity.EVENT;

@Service
public class AdapterDataHandlerImpl implements AdapterDataHandler{
    private Logger LOG = LoggerFactory.getLogger(AdapterDataHandlerImpl.class);

    @Autowired
    OriginServiceImpl originService;

    @Autowired
    @Qualifier("MispAppClient")
    MispAppClient mispAppClient;

    @Autowired
    ElasticClient elasticClient;

    @Autowired
    EmitterDataHandler emitterDataHandler;

    @Value("${server.name}")
    String cspId;

    HttpStatus status;

    JsonNode jsonNode;
    JsonNode eventCreatedUpdated;

    private static final Configuration configuration = Configuration.builder()
            .options(Option.ALWAYS_RETURN_LIST, Option.SUPPRESS_EXCEPTIONS)
            .jsonProvider(new JacksonJsonNodeJsonProvider())
            .mappingProvider(new JacksonMappingProvider())
            .build();

    @Override
    public ResponseEntity<String> handleIntegrationData(IntegrationData integrationData, String requestMethod) {

        LOG.debug(integrationData.getDataObject().toString());
        LOG.debug(integrationData.getDataParams().toString());
        String uuid = integrationData.getDataParams().getOriginRecordId();

        jsonNode = new ObjectMapper().convertValue(integrationData.getDataObject(), JsonNode.class);
        LOG.debug(jsonNode.toString());
        try {
            LOG.debug(new ObjectMapper().writeValueAsString(integrationData.getDataObject()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        List<Origin> origins = originService.findByOriginRecordId(uuid);
        if (origins.isEmpty()){
            LOG.debug("Entry for " + uuid + " not found.");
            Origin origin = new Origin();
            origin.setOriginCspId(integrationData.getDataParams().getOriginCspId());
            origin.setOriginApplicationId(integrationData.getDataParams().getOriginApplicationId());
            origin.setOriginRecordId(integrationData.getDataParams().getOriginRecordId());
            origin.setCspId(cspId);
            origin.setApplicationId("misp");
            origin.setRecordId(integrationData.getDataParams().getOriginRecordId());
            Origin org = originService.saveOrUpdate(origin);
            LOG.debug("Origin inserted: " + org);
        }
        else {
            LOG.debug("Origin params already found in table");
        }


        /*ce
        Search and handle RTIR objects
         */
        integrationData = handleRTIR(integrationData);
        integrationData = handleVulnerability(integrationData);
        /*
        Remake jsonNode
         */
        jsonNode = new ObjectMapper().convertValue(integrationData.getDataObject(), JsonNode.class);
        eventCreatedUpdated = new ObjectMapper().createObjectNode();

        integrationData.getSharingParams().setToShare(false);

        LOG.debug("requestMethod: " + requestMethod);
        if (!requestMethod.equals("DELETE")) {
            try {
                handleEventAddEdit();
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }


        /**
         * Handle Proposals
         */
        ReadContext ctx = JsonPath.using(configuration).parse(jsonNode);

        // Add New Attribute Proposals
        String eventId = String.valueOf(eventCreatedUpdated.get("Event").get("id").textValue());
        List<LinkedHashMap> eventShadowAttributes = ctx.read("$.Event.ShadowAttribute[*]", List.class);
        handleShadowAttributeAdd(eventShadowAttributes, eventId);

        // Edit Existing Attribute Proposals
        List<LinkedHashMap> attributeShadowAttributes = ctx.read("$.Event.Attribute[*].ShadowAttribute[*]", List.class);
        handleShadowAttributeEdit(attributeShadowAttributes);


        /**
         * issue: SXCSP-339
         * Implement reemition flow
         */
        try {
            emitterDataHandler.handleReemittionMispData(integrationData, MispContextUrl.MispEntity.EVENT, false, true);
        }
        catch (Exception e){
            LOG.error("RE - EMITTION FAILED: ", e);
        }


        return new ResponseEntity<String>(status);
    }

    private void handleEventAddEdit() throws IOException {
        // set "published":false
        ((ObjectNode) jsonNode.get("Event")).put("published", new Boolean(false));

        try {
            ResponseEntity<String> responseEntity = mispAppClient.addMispEvent(jsonNode.toString());
            eventCreatedUpdated = new ObjectMapper().readValue(responseEntity.getBody(), JsonNode.class);
            status = responseEntity.getStatusCode();
            LOG.debug(responseEntity.toString());
        } catch (StatusCodeException e) {
            LOG.error(e.getMessage());
            if (!e.getHttpHeaders().get("location").isEmpty()) {
                String location = e.getHttpHeaders().get("location").get(0);
                LOG.debug("" + location);
//                    jsonNode = ((ObjectNode) jsonNode.get("Event")).put("timestamp", String.valueOf(Instant.now().getEpochSecond() + 1));
                LOG.debug(jsonNode.toString());
                ResponseEntity<String> responseEntity = mispAppClient.updateMispEvent(location, jsonNode.toString());
                status = responseEntity.getStatusCode();
                eventCreatedUpdated = new ObjectMapper().readValue(responseEntity.getBody(), JsonNode.class);
                if (eventCreatedUpdated.get("message") != null && eventCreatedUpdated.get("message").textValue().toLowerCase().equals("error")){
                    LOG.error(eventCreatedUpdated.toString());
                    eventCreatedUpdated = jsonNode;
                }
            }
        }
    }
    private void handleShadowAttributeAdd(List<LinkedHashMap> eventShadowAttributes, String eventId){

        LOG.debug("HANDLING SHADOW ATTRIBUTES");

        eventShadowAttributes.forEach(shadowAttribute -> {
            LOG.debug(shadowAttribute.toString());
            String shadowAttributeJsonString = null;
            try {
                shadowAttributeJsonString = (new ObjectMapper()).writeValueAsString(shadowAttribute);
                LOG.debug("PROPOSAL ADD: " + shadowAttributeJsonString);
                ResponseEntity responseEntity = mispAppClient.addMispProposal(eventId ,shadowAttributeJsonString);
                LOG.debug("PROPOSAL ADD RESPONSE:" + responseEntity.toString());
            } catch (JsonProcessingException e) {
                LOG.error("Could not parse shadowattribute: " + e);
            } catch (StatusCodeException e) {
                LOG.error(e.getMessage());
                String location = e.getHttpHeaders().get("location").get(0);
                LOG.debug("" + location);
                ResponseEntity responseEntity = mispAppClient.updateMispProposal(location, shadowAttributeJsonString);
                LOG.debug("PROPOSAL EDIT RESPONSE<exc>:" + responseEntity.toString());
            }
        });
    }

    private void handleShadowAttributeEdit(List<LinkedHashMap> attributeShadowAttributes){

        attributeShadowAttributes.forEach(shadowAttribute -> {
            LOG.debug(shadowAttribute.toString());

            // Get Attribute by UUID
            String attrUuid = String.valueOf(shadowAttribute.get("uuid"));
            JsonNode attribute = new ObjectMapper().createObjectNode();
            LOG.debug("PROPOSAL EDIT: " + attribute.toString());
            try {
                attribute = new ObjectMapper().readValue(mispAppClient.getMispAttribute(attrUuid).getBody(), JsonNode.class);
                LOG.debug("FETCHING LOCAL ATTRIBUTE: " + attribute.toString());
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
            String attrId = attribute.get("Attribute").get("id").textValue();
            LOG.debug("Attribute id: "  + attrId);
            ObjectNode shadowAttributeRequestNode = new ObjectMapper().createObjectNode();
            String shadowAttributeJsonString = null;
            try {
                shadowAttributeJsonString = (new ObjectMapper()).writeValueAsString(shadowAttribute);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            try {
                ShadowAttributeRequestDTO shadowAttributeRequest = new ShadowAttributeRequestDTO(new ShadowAttributeRequestDTO.AttributeRequest(shadowAttribute));
                String attrRequestStr = new ObjectMapper().writeValueAsString(shadowAttributeRequest);
                ResponseEntity responseEntity = mispAppClient.updateMispProposal(attrId , attrRequestStr);
                LOG.debug("PROPOSAL EDIT RESPONSE:" + responseEntity.toString());
            } catch (StatusCodeException e) {
                LOG.error(e.getMessage());
                String location = e.getHttpHeaders().get("location").get(0);
                LOG.debug("" + location);
                ResponseEntity responseEntity = mispAppClient.updateMispProposal(location, shadowAttributeJsonString);
                LOG.debug("PROPOSAL EDIT RESPONSE<exc>:" + responseEntity.toString());
            } catch (JsonProcessingException e) {
                LOG.error(e.getMessage());
            }
        });

    }

    private IntegrationData handleRTIR(IntegrationData ilData) {
        IntegrationData modifiedIlData = ilData;

        //Convert Integration Data to JSON
        JsonNode jsonNode = new ObjectMapper().convertValue(modifiedIlData.getDataObject(), JsonNode.class);

        /**
         * Issue: SXCSP-340, SXCSP-341
         * Reference resolving in RTIR objects
         */
        String title = "";
        String url = "";
        String recordId = "";
        String originCspId = "";
        String originRecordId = "";
        String applicationId = MispContextUrl.RTIREntity.APPLICATION_ID.toString();

        if (jsonNode.get(EVENT.toString()).has("Object")) {
            for (JsonNode jn : jsonNode.get(EVENT.toString()).get("Object")){
                if (jn.get("name").textValue().toLowerCase().equals(MispContextUrl.RTIREntity.RTIR_NAME.toString().toLowerCase())){
                    for (JsonNode ja : jn.get("Attribute")){
                        if (ja.get("object_relation").textValue().toLowerCase().equals(MispContextUrl.RTIREntity.MAP_CSP_URL_VALUE.toString().toLowerCase()) &&
                                url.length() == 0) {
                            url = ja.get("value").textValue();
                            LOG.debug("============RTIR url: " + url);
                        }
                        if (ja.get("object_relation").textValue().toLowerCase().equals(MispContextUrl.RTIREntity.MAP_ORIGIN_CSP_ID_VALUE.toString().toLowerCase()) &&
                                originCspId.length() == 0) {
                            originCspId = ja.get("value").textValue();
                            LOG.debug("============RTIR originCspId: " + originCspId);
                        }
                        if (ja.get("object_relation").textValue().toLowerCase().equals(MispContextUrl.RTIREntity.MAP_ORIGIN_RECORD_ID_VALUE.toString().toLowerCase()) &&
                                originRecordId.length() == 0) {
                            originRecordId = ja.get("value").textValue();
                            LOG.debug("============RTIR originRecordId: " + originRecordId);
                        }
                        if (ja.get("object_relation").textValue().toLowerCase().equals(MispContextUrl.RTIREntity.MAP_TITLE.toString().toLowerCase()) &&
                                title.length() == 0) {
                            title = ja.get("value").textValue();
                            LOG.debug("============RTIR title: " + title);
                        }
                        if (ja.get("object_relation").textValue().toLowerCase().equals(MispContextUrl.RTIREntity.MAP_TICKET_NO.toString().toLowerCase()) &&
                                recordId.length() == 0) {
                            recordId = ja.get("value").textValue();
                            LOG.debug("============RTIR recordId: " + recordId);
                        }
                    }


                    /**
                     1. The adapter queries ES for the existence of an "incident" with the given origin cspid/appid/id
                     2. If it finds such incidence, it rewrites the incidence url inside the RTIR object with the url that this incidence has in ES
                     3. If not It deletes the url field
                     4. It pushes the event with the rewriten RTIR fields to misp
                     */
                    String newURL = "";
                    String newTickerNumber = "";
                    try {
                        IntegrationData searchData = new IntegrationData();
                        DataParams searchParams = new DataParams();
                        searchParams.setOriginRecordId(originRecordId);
                        searchParams.setOriginApplicationId(applicationId);
                        searchParams.setOriginCspId(originCspId);

                        searchData.setDataParams(searchParams);
                        searchData.setDataType(IntegrationDataType.INCIDENT);

                        JsonNode esObject = elasticClient.getESobjectFromOrigin(searchData);
                        if (esObject != null) {
                            LOG.debug("Found in elastic");
                            newURL = esObject.get("dataParams").get("url").textValue();

                            /**
                             * SXCSP-386
                             */
                            newTickerNumber = esObject.get("dataObject").get("id").textValue();
                        }
                        else {
                            LOG.debug("NOT Found in elastic");
                        }
                        //replace
                        for (JsonNode ja : jn.get("Attribute")){
                            if (ja.get("object_relation").textValue().toLowerCase().equals(MispContextUrl.RTIREntity.MAP_CSP_URL_VALUE.toString().toLowerCase())) {
                                ((ObjectNode)ja).put("value",  newURL);
                            }
                            if (ja.get("object_relation").textValue().toLowerCase().equals(MispContextUrl.RTIREntity.MAP_TICKET_NO.toString().toLowerCase()) ) {
                                ((ObjectNode)ja).put("value",  newTickerNumber);
                            }
                        }
                    }
                    catch (Exception e){
                        LOG.info(e.getMessage());
                    }
                    LOG.info("RTIR new URL: " + newURL);

                }
            }
        }

        //update IntegrationData
        modifiedIlData.setDataObject(jsonNode);
        LOG.debug("MODIFIED IL DATA: " + modifiedIlData.toString());

        return modifiedIlData;
    }

    private IntegrationData handleVulnerability(IntegrationData ilData) {
        IntegrationData modifiedIlData = ilData;

        //Convert Integration Data to JSON
        JsonNode jsonNode = new ObjectMapper().convertValue(modifiedIlData.getDataObject(), JsonNode.class);

        /**
         * Issue: SXCSP-380: link vulnerability to event/threat just like RTIR
         */
        String title = "";
        String url = "";
        String recordId = "";
        String originCspId = "";
        String originRecordId = "";
        String applicationId = MispContextUrl.VULNERABILITYEntity.APPLICATION_ID.toString();

        if (jsonNode.get(EVENT.toString()).has("Object")) {
            for (JsonNode jn : jsonNode.get(EVENT.toString()).get("Object")){
                if (jn.get("name").textValue().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.VULNERABILITY_NAME.toString().toLowerCase())){
                    for (JsonNode ja : jn.get("Attribute")){
                        if (ja.get("object_relation").textValue().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_CSP_URL_VALUE.toString().toLowerCase()) &&
                                url.length() == 0) {
                            url = ja.get("value").textValue();
                            LOG.debug("============VULNERABILITY url: " + url);
                        }
                        if (ja.get("object_relation").textValue().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_ORIGIN_CSP_ID_VALUE.toString().toLowerCase()) &&
                                originCspId.length() == 0) {
                            originCspId = ja.get("value").textValue();
                            LOG.debug("============VULNERABILITY originCspId: " + originCspId);
                        }
                        if (ja.get("object_relation").textValue().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_ORIGIN_RECORD_ID_VALUE.toString().toLowerCase()) &&
                                originRecordId.length() == 0) {
                            originRecordId = ja.get("value").textValue();
                            LOG.debug("============VULNERABILITY originRecordId: " + originRecordId);
                        }
                        if (ja.get("object_relation").textValue().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_TITLE_RELATION.toString().toLowerCase()) &&
                                ja.get("category").toString().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_TITLE_CATEGORY.toString().toLowerCase()) &&
                                title.length() == 0) {
                            title = ja.get("value").textValue();
                            LOG.debug("============VULNERABILITY title: " + title);
                        }
                        if (ja.get("object_relation").textValue().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_RECORD_RELATION.toString().toLowerCase()) &&
                                ja.get("category").toString().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_RECORD_CATEGORY.toString().toLowerCase()) &&
                                recordId.length() == 0) {
                            recordId = ja.get("value").textValue();
                            LOG.debug("============VULNERABILITY recordId: " + recordId);
                        }
                    }

                    /**
                     1. The adapter queries ES for the existence of an "incident" with the given origin cspid/appid/id
                     2. If it finds such incidence, it rewrites the incidence url inside the RTIR object with the url that this incidence has in ES
                     3. If not It deletes the url field
                     4. It pushes the event with the rewriten RTIR fields to misp
                     */
                    String newURL = "";
                    String newTickerNumber = "";
                    try {
                        IntegrationData searchData = new IntegrationData();
                        DataParams searchParams = new DataParams();
                        searchParams.setOriginRecordId(originRecordId);
                        searchParams.setOriginApplicationId(applicationId);
                        searchParams.setOriginCspId(originCspId);

                        searchData.setDataParams(searchParams);
                        searchData.setDataType(IntegrationDataType.VULNERABILITY);

                        JsonNode esObject = elasticClient.getESobjectFromOrigin(searchData);
                        if (esObject != null) {
                            LOG.info("FOUND TRUE");
                            newURL = esObject.get("dataParams").get("url").textValue();
                            //newTickerNumber = esObject.get("dataObject").get("id").textValue();
                        }
                        else {
                            LOG.info("NOT FOUND");
                        }
                        //replace
                        for (JsonNode ja : jn.get("Attribute")){
                            if (ja.get("object_relation").textValue().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_CSP_URL_VALUE.toString().toLowerCase()) ) {
                                ((ObjectNode)ja).put("value",  newURL);
                            }
                        /*
                        if (ja.get("object_relation").toString().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_RECORD_RELATION.toString().toLowerCase()) &&
                                ja.get("category").toString().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_RECORD_CATEGORY.toString().toLowerCase()) ) {
                            ((ObjectNode)ja).put("value",  newTickerNumber);
                        }
                        */
                        }
                    }
                    catch (Exception e){
                        LOG.info(e.getMessage());
                    }
                    LOG.info("VULNERABILITY new URL: " + newURL);
                    //LOG.info("VULNERABILITY new ID: " + newTickerNumber);

                }
            }
        }

        //update IntegrationData
        modifiedIlData.setDataObject(jsonNode);

        return modifiedIlData;
    }
}
