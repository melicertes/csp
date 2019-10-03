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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import static com.intrasoft.csp.misp.commons.config.MispContextUrl.MispEntity.EVENT;

@Service
public class AdapterDataHandlerImpl implements AdapterDataHandler {
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

    final ObjectMapper mapper = new ObjectMapper();

    private static final Configuration configuration = Configuration.builder()
            .options(Option.ALWAYS_RETURN_LIST, Option.SUPPRESS_EXCEPTIONS)
            .jsonProvider(new JacksonJsonNodeJsonProvider())
            .mappingProvider(new JacksonMappingProvider())
            .build();

    @Override
    public ResponseEntity<String> handleIntegrationData(IntegrationData integrationData, String requestMethod) {

        LOG.trace("RECEIVED: {} -> {} ",integrationData.getDataParams(), integrationData.toString());
        String uuid = integrationData.getDataParams().getOriginRecordId();
        Object getLocalEventResponseBody = integrationData.getDataObject();
        HttpStatus lastStatus = HttpStatus.OK;

        LOG.info("Handle integration data [type {} uuid {}] -from-> {}", integrationData.getDataType(), uuid, integrationData.getDataParams());

        try {
            LOG.info("uuid {} -> Fetching local event (if exists) based on the uuid of the incoming event.", uuid);
            getLocalEventResponseBody = mispAppClient.getMispEvent(uuid).getBody();
        } catch (StatusCodeException e) {
            LOG.error("Event for uuid {} not found, probably a new event: {} code: {}", uuid, integrationData.getDataType(), e.getStatusCode() );
        }

       // final String localEventId = safeExtractEventId(mapper.convertValue(getLocalEventResponseBody, JsonNode.class)); //get the local MISP event id

        List<Origin> origins = originService.findByOriginRecordId(uuid);

        LOG.info("uuid {} origins detected {}", uuid, origins);
        if (origins.isEmpty()) {
            LOG.info("Entry for " + uuid + " not found.");
            Origin origin = new Origin();
            origin.setOriginCspId(integrationData.getDataParams().getOriginCspId());
            origin.setOriginApplicationId(integrationData.getDataParams().getOriginApplicationId());
            origin.setOriginRecordId(integrationData.getDataParams().getOriginRecordId());
            origin.setCspId(cspId);
            origin.setApplicationId("misp");
            origin.setRecordId(integrationData.getDataParams().getOriginRecordId());
            Origin org = originService.saveOrUpdate(origin);
            LOG.info("Origin inserted: {}", org);
        } else {
            LOG.info("Origin {} of {} params already found in table", origins, uuid);
        }

        integrationData.getSharingParams().setToShare(false);

        handleRTIRintegration(integrationData);

        handleVulnerabilityIntegration(integrationData);

        /*
        Remake jsonNode
         */
        JsonNode jsonNode = mapper.convertValue(integrationData.getDataObject(), JsonNode.class);

        LOG.info("Handling event type {} uuid {} to misp", integrationData.getDataType(), uuid);

        MispEventContext eventContext = processMispEvent(integrationData, requestMethod, jsonNode);
        // update our variables
        jsonNode = eventContext.getNode();
        lastStatus = eventContext.getStatus();

        /**
         * Handle Proposals
         */
        ReadContext ctx = JsonPath.using(configuration).parse(jsonNode);

        // Add New Attribute Proposals
        String eventId = safeExtractEventId(jsonNode);
        LOG.info("Event id created/updated: " + eventId);
        List<LinkedHashMap> eventShadowAttributes = ctx.read("$.Event.ShadowAttribute[*]", List.class);

        LOG.info("uuid {} Handle event shadow attributes", uuid);

        eventShadowAttributes.forEach(shadowAttribute -> {
            LOG.info("Working with uuid {} shadowAttribute {}", uuid, shadowAttribute.toString());
            String shadowAttributeJsonString = null;
            try {
                shadowAttributeJsonString = (mapper).writeValueAsString(shadowAttribute);
                LOG.info("{} Add proposal request: {} ", uuid,  shadowAttributeJsonString);
                ResponseEntity responseEntity = mispAppClient.addMispProposal(eventId, shadowAttributeJsonString);
                LOG.info("{} Add proposal response: {}", uuid, responseEntity.toString());
            } catch (JsonProcessingException e1) {
                LOG.error("Could not parse shadow attribute {} ", e1.getMessage(), e1);
            } catch (StatusCodeException e2) {
                LOG.error("Status exception {}", e2.getMessage(), e2);
                String location = e2.getHttpHeaders().get("location").get(0);
                LOG.info("Possible redirect location found for attribute: {} -> {}", shadowAttribute,  location);
                ResponseEntity responseEntity = mispAppClient.updateMispProposal(location, shadowAttributeJsonString);
                LOG.info("{} Add proposal response: {}", uuid, responseEntity.toString());
            }
        });

        // Edit Existing Attribute Proposals
        List<LinkedHashMap> attributeShadowAttributes = ctx.read("$.Event.Attribute[*].ShadowAttribute[*]", List.class);

        extractShadowAttributes(attributeShadowAttributes);

        // Edit Existing Object Proposals
        List<LinkedHashMap> objectAttributeShadowAttributes = ctx.read("$.Event.Object[*].Attribute[*].ShadowAttribute[*]", List.class);

        extractShadowAttributes(objectAttributeShadowAttributes);

        try {
            emitterDataHandler.handleMispData(jsonNode, EVENT, true, false);
        } catch (IOException e) {
            LOG.error("Reemition failed: " + e.getMessage());
        }
        return new ResponseEntity<String>(lastStatus);
    }

    private MispEventContext processMispEvent(IntegrationData integrationData, String requestMethod, JsonNode jsonNode) {
        HttpStatus lastStatus = null;
        //response by default is the incoming json
        JsonNode responseJsonNode = jsonNode;
        String uuid = integrationData.getDataParams().getOriginRecordId();

        if (!requestMethod.equals("DELETE")) {
            try {
                LOG.info("uuid {} Handle Event add/edit action", uuid);
                // set "published":false
                //((ObjectNode) jsonNode.get("Event")).put("published", new Boolean(false));

                if (!integrationData.getDataParams().getOriginCspId().equals(integrationData.getDataParams().getCspId())) {
                    LOG.warn("uuid {} Cannot edit an event that I do not own! {} ", uuid, integrationData.getDataParams().getOriginCspId() + " -- " + integrationData.getDataParams().getCspId());
                    lastStatus = HttpStatus.OK;
                } else {
                    LOG.info("Event received from its origin, Remove proposals for new attributes if exist.");
                    JsonNode arrNode1 = jsonNode.get("Event").get("ShadowAttribute");
                    if (arrNode1 != null && arrNode1.isArray()) {
                        for (JsonNode jsonNode1 : arrNode1) {
                            LOG.info("uuid {} New Attribute proposal to remove {} ", uuid,  jsonNode1.toString());
                            mispAppClient.deleteMispProposal(jsonNode1.get("id").textValue());
                        }
                    }

                    LOG.info("Event {} -> Remove existing proposals from attributes if exist.", uuid);
                    JsonNode arrNode = jsonNode.get("Event").get("Attribute");
                    if (arrNode != null && arrNode.isArray()) {
                        for (JsonNode jsonNode1 : arrNode) {
                            JsonNode attrProposals = jsonNode1.get("ShadowAttribute");
                            if (attrProposals != null && attrProposals.isArray()) {
                                for (JsonNode jn : attrProposals) {
                                    LOG.info("uuid {} Proposal from existing Attribute to remove:{} ", uuid,  jn.toString());
                                    mispAppClient.deleteMispProposal(jn.get("id").textValue());
                                }
                            }
                        }
                    }

                    LOG.info("Event {} ->  Remove existing proposals from objects if exist.", uuid);
                    JsonNode arrNode3 = jsonNode.get("Event").get("Object");
                    if (arrNode3 != null && arrNode3.isArray()) {
                        for (JsonNode jsonNode1 : arrNode3) {
                            JsonNode attrProposals = jsonNode1.get("Attribute");
                            if (attrProposals != null && attrProposals.isArray()) {
                                for (JsonNode jsonNode3 : attrProposals) {
                                    JsonNode attrProposals2 = jsonNode3.get("ShadowAttribute");
                                    if (attrProposals2 != null && attrProposals2.isArray()) {
                                        for (JsonNode jn : attrProposals2) {
                                            LOG.info("uuid {} Proposal from existing Object Attribute to remove:{} ", uuid,  jn.toString());
                                            mispAppClient.deleteMispProposal(jn.get("id").textValue());
                                        }
                                    }
                                }
                            }
                        }
                    }

                    try {
                        // SXCSP-503: Change Distribution to one lower state (2 -> 1, 1 -> 0)
                        JsonNode locatedNode = jsonNode.path(MispContextUrl.MispEntity.EVENT.toString()).path("distribution");
                        final int eventDistributionLevel =  Integer.parseInt(locatedNode.textValue());;
                        if (eventDistributionLevel == 2 || eventDistributionLevel == 1) {
                            ((ObjectNode) jsonNode).findParent("distribution").put("distribution", String.valueOf(eventDistributionLevel - 1));
                        }

                        ResponseEntity<String> responseEntity = mispAppClient.addMispEvent(jsonNode.toString());
                        responseJsonNode = mapper.readValue(responseEntity.getBody(), JsonNode.class);
                        String responseEventId = safeExtractEventId(responseJsonNode);

                        lastStatus = responseEntity.getStatusCode();
                        LOG.debug("uuid {}, response -> {}, ",uuid, responseEntity.toString());
                        LOG.info("Event {}  edit/add action result: {}", responseEventId,  responseEntity.getStatusCode().toString());
                    } catch (StatusCodeException e) {
                        LOG.error("Status exception {}", e.getMessage(), e);
                        if (!e.getHttpHeaders().get("location").isEmpty()) {
                            String location = e.getHttpHeaders().get("location").get(0);
                            LOG.info("uuid {} redirect location: {} ", uuid,  location);
                            ResponseEntity<String> responseEntity = mispAppClient.updateMispEvent(location, jsonNode.toString());
                            lastStatus = responseEntity.getStatusCode();
                            responseJsonNode = mapper.readValue(responseEntity.getBody(), JsonNode.class);
                            if (responseJsonNode.get("message") != null && responseJsonNode.get("message").textValue().toLowerCase().equals("error")) {
                                LOG.error("Retry of operation for uuid {} failed, error was {}", uuid, responseJsonNode.toString());
                                responseJsonNode = jsonNode;
                            }
                            LOG.info("Event {} edit/add action result: {}", uuid, responseEntity.getStatusCode().toString());
                        }
                    }
                }
            } catch (IOException e) {
                LOG.error("IO exception: {}", e.getMessage(), e);
                //TODO deal with it
            }
        }

        return new MispEventContext(responseJsonNode, lastStatus);
    }

    private void extractShadowAttributes(List<LinkedHashMap> attributeShadowAttributes) {
        attributeShadowAttributes.forEach(sa -> {
            LOG.info("Attribute {} ",sa.toString());

            // Get Attribute by UUID
            String attrUuid1 = String.valueOf(sa.get("uuid"));
            JsonNode attribute1 = mapper.createObjectNode();
            LOG.info("Edit proposal request: {} ", attribute1.toString());
            try {
                attribute1 = mapper.readValue(mispAppClient.postMispAttribute(attrUuid1).getBody(), JsonNode.class);
                LOG.info("Fetching local attribute: {} ", attribute1.toString());
            } catch (IOException e2) {
                LOG.error(e2.getMessage());
            }
            String attrId1 = attribute1.get("response").get("Attribute").get("id").textValue();
            LOG.info("Attribute id: " + attrId1);
            ObjectNode shadowAttributeRequestNode1 = mapper.createObjectNode();
            String shadowAttributeJsonString1 = null;
            try {
                shadowAttributeJsonString1 = (mapper).writeValueAsString(sa);
            } catch (JsonProcessingException e2) {
                LOG.error(e2.getMessage());
            }
            try {
                ShadowAttributeRequestDTO shadowAttributeRequest1 = new ShadowAttributeRequestDTO(new ShadowAttributeRequestDTO.AttributeRequest(sa));
                String attrRequestStr1 = mapper.writeValueAsString(shadowAttributeRequest1);
                ResponseEntity responseEntity1 = mispAppClient.updateMispProposal(attrId1, attrRequestStr1);
                LOG.info("Edit proposal response: " + responseEntity1.toString());
            } catch (StatusCodeException e2) {
                LOG.error("Proposale processing fail {}", e2.getMessage());
                String location1 = e2.getHttpHeaders().get("location").get(0);
                LOG.info("Redirect location {} ",  location1);
                ResponseEntity responseEntity1 = mispAppClient.updateMispProposal(location1, shadowAttributeJsonString1);
                LOG.info("Edit proposal response: {}", responseEntity1.toString());
            } catch (JsonProcessingException e2) {
                LOG.error(e2.getMessage());
            }
        });
    }

    private void handleVulnerabilityIntegration(IntegrationData integrationData) {
        String uuid = integrationData.getDataParams().getOriginRecordId();

        LOG.info("Handle Vulnerability for {} if present.", uuid );
        //Convert Integration Data to JSON
        JsonNode vulnDataNode = mapper.convertValue(integrationData.getDataObject(), JsonNode.class);

        /**
         * Issue: SXCSP-380: link vulnerability to event/threat just like RTIR
         */
        String title1 = "";
        String url1 = "";
        String recordId1 = "";
        String originCspId1 = "";
        String originRecordId1 = "";
        String applicationId1 = MispContextUrl.VULNERABILITYEntity.APPLICATION_ID.toString();

        if (vulnDataNode.get(EVENT.toString()).has("Object")) {
            for (JsonNode jn1 : vulnDataNode.get(EVENT.toString()).get("Object")) {
                if (jn1.get("name").textValue().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.VULNERABILITY_NAME.toString().toLowerCase())) {
                    for (JsonNode ja : jn1.get("Attribute")) {
                        if (ja.get("object_relation").textValue().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_CSP_URL_VALUE.toString().toLowerCase()) &&
                                url1.length() == 0) {
                            url1 = ja.get("value").textValue();
                            LOG.debug("============VULNERABILITY url: " + url1);
                        }
                        if (ja.get("object_relation").textValue().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_ORIGIN_CSP_ID_VALUE.toString().toLowerCase()) &&
                                originCspId1.length() == 0) {
                            originCspId1 = ja.get("value").textValue();
                            LOG.debug("============VULNERABILITY originCspId: " + originCspId1);
                        }
                        if (ja.get("object_relation").textValue().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_ORIGIN_RECORD_ID_VALUE.toString().toLowerCase()) &&
                                originRecordId1.length() == 0) {
                            originRecordId1 = ja.get("value").textValue();
                            LOG.info("============VULNERABILITY originRecordId: " + originRecordId1);
                        }
                        if (ja.get("object_relation").textValue().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_TITLE_RELATION.toString().toLowerCase()) &&
                                ja.get("category").toString().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_TITLE_CATEGORY.toString().toLowerCase()) &&
                                title1.length() == 0) {
                            title1 = ja.get("value").textValue();
                            LOG.debug("============VULNERABILITY title: " + title1);
                        }
                        if (ja.get("object_relation").textValue().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_RECORD_RELATION.toString().toLowerCase()) &&
                                ja.get("category").toString().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_RECORD_CATEGORY.toString().toLowerCase()) &&
                                recordId1.length() == 0) {
                            recordId1 = ja.get("value").textValue();
                            LOG.info("uuid {} ==VULNERABILITY recordId: {} ", uuid, recordId1);
                        }
                    }

                    /**
                     1. The adapter queries ES for the existence of an "incident" with the given origin cspid/appid/id
                     2. If it finds such incidence, it rewrites the incidence url inside the RTIR object with the url that this incidence has in ES
                     3. If not It deletes the url field
                     4. It pushes the event with the rewriten RTIR fields to misp
                     */
                    String newURL = "";
                    try {
                        IntegrationData searchData = new IntegrationData();
                        DataParams searchParams = new DataParams();
                        searchParams.setOriginRecordId(originRecordId1);
                        searchParams.setOriginApplicationId(applicationId1);
                        searchParams.setOriginCspId(originCspId1);

                        searchData.setDataParams(searchParams);
                        searchData.setDataType(IntegrationDataType.VULNERABILITY);

                        JsonNode esObject = elasticClient.getESobjectFromOrigin(searchData);
                        if (esObject != null) {
                            newURL = esObject.get("dataParams").get("url").textValue();
                            LOG.info("ES FOUND uuid {} url {} ", uuid, newURL);

                            //newTickerNumber = esObject.get("dataObject").get("id").textValue();
                        } else {
                            LOG.info("ES NOT FOUND uuid {}", uuid);
                        }
                        //replace
                        for (JsonNode ja : jn1.get("Attribute")) {
                            if (ja.get("object_relation").textValue().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_CSP_URL_VALUE.toString().toLowerCase())) {
                                ((ObjectNode) ja).put("value", newURL);
                            }
                        /*
                        if (ja.get("object_relation").toString().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_RECORD_RELATION.toString().toLowerCase()) &&
                                ja.get("category").toString().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_RECORD_CATEGORY.toString().toLowerCase()) ) {
                            ((ObjectNode)ja).put("value",  newTickerNumber);
                        }
                        */
                        }
                    } catch (Exception e3) {
                        LOG.error("Exception connecting to ES, error {}", e3.getMessage());
                    }
                    //LOG.info("VULNERABILITY new ID: " + newTickerNumber);

                }
            }
        }

        //update IntegrationData
        integrationData.setDataObject(vulnDataNode);
    }

    private void handleRTIRintegration(IntegrationData integrationData) {
        /**
         *Search and handle RTIR objects
         */
        String uuid = integrationData.getDataParams().getOriginRecordId();

        LOG.info("Handle RTIR for uuid {} if present.", uuid);
        //Convert Integration Data to JSON
        JsonNode rtJsonNode = mapper.convertValue(integrationData.getDataObject(), JsonNode.class);

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

        if (rtJsonNode.get(EVENT.toString()).has("Object")) {
            for (JsonNode jn1 : rtJsonNode.get(EVENT.toString()).get("Object")) {
                if (jn1.get("name").textValue().toLowerCase().equals(MispContextUrl.RTIREntity.RTIR_NAME.toString().toLowerCase())) {
                    for (JsonNode ja : jn1.get("Attribute")) {
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
                            LOG.info("============RTIR title: " + title);
                        }
                        if (ja.get("object_relation").textValue().toLowerCase().equals(MispContextUrl.RTIREntity.MAP_TICKET_NO.toString().toLowerCase()) &&
                                recordId.length() == 0) {
                            recordId = ja.get("value").textValue();
                            LOG.info("uuid {} ====RTIR recordId: {} ", uuid,  recordId);
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
                            LOG.info("Found in elastic");
                            newURL = esObject.get("dataParams").get("url").textValue();

                            /**
                             * SXCSP-386
                             */
                            newTickerNumber = esObject.get("dataObject").get("id").textValue();
                        } else {
                            LOG.info("NOT Found in elastic");
                        }
                        //replace
                        for (JsonNode ja : jn1.get("Attribute")) {
                            if (ja.get("object_relation").textValue().toLowerCase().equals(MispContextUrl.RTIREntity.MAP_CSP_URL_VALUE.toString().toLowerCase())) {
                                ((ObjectNode) ja).put("value", newURL);
                            }
                            if (ja.get("object_relation").textValue().toLowerCase().equals(MispContextUrl.RTIREntity.MAP_TICKET_NO.toString().toLowerCase())) {
                                ((ObjectNode) ja).put("value", newTickerNumber);
                            }
                        }
                    } catch (Exception e3) {
                        LOG.error(e3.getMessage());
                    }
                    LOG.info("RTIR new URL: " + newURL);

                }
            }
        }

        //update IntegrationData
        integrationData.setDataObject(rtJsonNode);
        LOG.debug("MODIFIED IL DATA: " + integrationData.toString());

        //--- end rtir
    }

    private String safeExtractEventId(JsonNode jsonNode) {
        if (Objects.nonNull(jsonNode.get(EVENT.name())) && Objects.nonNull(jsonNode.get(EVENT.name()).get("id"))) {
            return jsonNode.get(EVENT.name()).get("id").textValue();
        }
        LOG.error("Using " + jsonNode.toString() + " no event Id could be extracted. ");
        return "";
    }

    static class MispEventContext {
        private JsonNode node;
        private HttpStatus status;

        MispEventContext(JsonNode node, HttpStatus status) {
            this.node = node;
            this.status = status;
        }

        public JsonNode getNode() {
            return node;
        }

        public void setNode(JsonNode node) {
            this.node = node;
        }

        public HttpStatus getStatus() {
            return status;
        }

        public void setStatus(HttpStatus status) {
            this.status = status;
        }

    }

}
