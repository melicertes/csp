package com.intrasoft.csp.misp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intrasoft.csp.client.CspClient;
import com.intrasoft.csp.client.ElasticClient;
import com.intrasoft.csp.commons.model.DataParams;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.SharingParams;
import com.intrasoft.csp.misp.client.MispAppClient;
import com.intrasoft.csp.misp.commons.config.MispContextUrl;
import com.intrasoft.csp.misp.domain.model.Origin;
import com.intrasoft.csp.misp.domain.service.impl.OriginServiceImpl;
import com.intrasoft.csp.misp.service.DistributionPolicyRectifier;
import com.intrasoft.csp.misp.service.EmitterDataHandler;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.*;

import static com.intrasoft.csp.misp.commons.config.MispContextUrl.MispEntity.ACTION;
import static com.intrasoft.csp.misp.commons.config.MispContextUrl.MispEntity.EVENT;

@Service
public class EmitterDataHandlerImpl implements EmitterDataHandler, MispContextUrl {

    final Logger LOG = LoggerFactory.getLogger(EmitterDataHandlerImpl.class);

    @Value("${misp.app.protocol}")
    String protocol;

    @Value("${misp.app.host}")
    String host;

    @Value("${misp.ui.host}")
    String uiHost;

    @Value("${misp.app.port}")
    String port;

    @Value("${misp.app.authorization.key}")
    String authorizationKey;

    @Value("${misp.app.events.path}")
    String eventsPath;

    @Value("${misp.sync.prefix}")
    String prefix;

    @Value("${misp.sync.type}")
    String syncType;

    @Value("${server.name}")
    String cspId;

    @Autowired
    CspClient cspClient;

    @Autowired
    OriginServiceImpl originService;

    @Autowired
    @Qualifier("MispAppClient")
    MispAppClient mispAppClient;

    @Autowired
    DistributionPolicyRectifier distributionPolicyRectifier;

    @Value("${elastic.protocol}")
    String elasticProtocol;
    @Value("${elastic.host}")
    String elasticHost;
    @Value("${elastic.port}")
    String elasticPort;
    @Value("${elastic.path}")
    String elasticPath;

    @Autowired
    ElasticClient elasticClient;

    private JsonNode jsonNode;

    private static final Configuration configuration = Configuration.builder()
            .options(Option.ALWAYS_RETURN_LIST, Option.AS_PATH_LIST, Option.SUPPRESS_EXCEPTIONS)
            .jsonProvider(new JacksonJsonNodeJsonProvider())
            .mappingProvider(new JacksonMappingProvider())
            .build();

    @Override
    public void handleMispData(Object object, MispEntity mispEntity, boolean isReEmittion, boolean isDelete) {

        jsonNode = new ObjectMapper().convertValue(object, JsonNode.class);

        LOG.info(jsonNode.toString());

        String uuid = "";
        Map<String, List<String>> eventValidationMap = new HashMap<>();

        LOG.info("Handling misp Event emission");

        if (isReEmittion){
            LOG.debug("Received re-emission.");
        }
        else {
            LOG.debug("Received from emitter.");
        }


        if (mispEntity.equals(EVENT)) {
            uuid = jsonNode.get(EVENT.toString()).get("uuid").textValue();
            LOG.debug("Event with uuid: " + uuid);
            eventValidationMap = eventValidation(jsonNode, LOG);
            // @TODO check for potential bug
            try{
                // TO REMOVE
                if (!isDelete){
                    object = mispAppClient.getMispEvent(uuid).getBody();
                    jsonNode = new ObjectMapper().convertValue(object, JsonNode.class);
                    distributionPolicyRectifier.rectifyEvent(jsonNode);
//                    jsonNode = updateTimestamp(new ObjectMapper().convertValue(object, JsonNode.class));
                }

            }
            catch (Exception e){
                LOG.error("Get Event from MISP API Failed: ", e);
                if (!isDelete){
                    return;
                }
            }
        }

        DataParams dataParams = new DataParams();
        dataParams.setDateTime(new DateTime());

        /** issue: SXCSP-332
         * origin fields
         * the originids should stay the same (read from confluence)
         * check local mapping table, if not found use our own values*/
        List<Origin> origins = originService.findByOriginRecordId(uuid);
        if (origins.isEmpty()){
            LOG.debug("Origin not found");
            dataParams.setOriginCspId(cspId);
            dataParams.setOriginApplicationId("misp");
            dataParams.setOriginRecordId(uuid);
            dataParams.setCspId(cspId);
            dataParams.setApplicationId("misp");
            dataParams.setRecordId(uuid);
        }
        else {
            LOG.debug("Origin found" + origins.toString());
            dataParams.setOriginCspId(origins.get(0).getOriginCspId());
            dataParams.setOriginApplicationId(origins.get(0).getOriginApplicationId());
            dataParams.setOriginRecordId(origins.get(0).getOriginRecordId());
            dataParams.setCspId(cspId);
            dataParams.setApplicationId("misp");
            dataParams.setRecordId(uuid);
        }


        /** Issue setUrl
         * get base url from application.properties
         * how does the url update from emitter of source to adapter of destination*/
        dataParams.setUrl(protocol + "://" + uiHost + ":" + port + "/events/" + uuid);

        SharingParams sharingParams = new SharingParams();
        sharingParams.setIsExternal(false);

        /** Issue: SXCSP-339 and SXCSP-452
         * setToShare
         * When the adapter receives data from an external CSP (the “isExternal” flag is set to TRUE)
         * the operation should trigger an emitter response. The emitter should emit this record (for indexing) and
         * set the “toShare” flag to FALSE (rest on conluence https://confluence.sastix.com/display/SXCSP/Integration+Layer+Flows).
         * set toShare flag to false when distribution is "This organization only"*/
        if (isReEmittion){
            sharingParams.setToShare(false);
        }
        else {
            /**
             * SXCSP-384: setToShare=True only if MISP event is published
             */
            Boolean eventPublished = Boolean.parseBoolean(jsonNode.get(EVENT.toString()).get("published").toString());
            sharingParams.setToShare(eventPublished);
            if (jsonNode.get(EVENT.toString()).get("distribution").textValue().equals("0")){
                sharingParams.setToShare(false);
            }
        }

        /** issue: SXCSP-337
         * setTcId and setTeamID
         * should be harvested from dataobject when we support for the user to specify tc or team as recipient
         * find out how to differentiate our custom shared groups from the normal ones
         * use custom sharing groups uuids as tcid, use custom organizations(?) uuids as team id.
         * harvest only from the dataobject part which dictates which organization or sharing group should get this event*/
        if (eventValidationMap.containsKey("org")) { // Valid Sharing Group with valid Organisations
            sharingParams.setTeamId(eventValidationMap.get("org"));
        } else if (eventValidationMap.containsKey("sg")) { // Valid Sharing Group without any valid organisations
            sharingParams.setTcId(eventValidationMap.get("sg"));
        }

        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataParams(dataParams);
        integrationData.setSharingParams(sharingParams);
        integrationData.setDataObject(jsonNode);

        /**
         * issue: SXCSP-333
         * define a classification to differentiate between threat/event
         */
        IntegrationDataType integrationDataType = IntegrationDataType.EVENT;
        if (jsonNode.get(EVENT.toString()).has("Tag")){
            for (JsonNode jn : jsonNode.get(EVENT.toString()).get("Tag")){
                if (jn.get("name").textValue().equals("threat")){
                    integrationDataType = IntegrationDataType.THREAT;
                }
            }
        }

        integrationData.setDataType(integrationDataType);

        /** issue: SXCSP-334
         * how to identify if it is post or put
         * should search in ES to see if this uuid exists
         * query the index based on datatype (event/threat), if found send put else send post */

        if (isDelete) {
            cspClient.deleteIntegrationData(integrationData);
            return;
        }

        LOG.debug("Integration Data Forwarded to IL: " + integrationData);

        boolean objextExists = false;
        try {
            objextExists = elasticClient.objectExists(integrationData);
        }
        catch (Exception e){
            LOG.error("Elastic query failed, " + e.getMessage());
        }

        LOG.debug("Object exists: " + objextExists);

        try {
            if (objextExists){
                cspClient.updateIntegrationData(integrationData);
            }
            else {
                cspClient.postIntegrationData(integrationData);
            }
        }
        catch (Exception e){
            LOG.error("Forward to IL failed with: ", e);
        }
    }

    private Map<String, List<String>> eventValidation(JsonNode jsonNode, Logger LOG) {
        Map<String, List<String>> result = new HashMap<>();
        String sharingGroupUuid = "";
        List<String> syncedOrgsUuids = new ArrayList<>();
        String sharingGroupName = new String();
        // Sharing Group / Organisations synchronization validation for use with Sharing Params
        try { // handling the case when there's no sharing group specified in the event's distribution setting.
            sharingGroupName = jsonNode.get(EVENT.toString()).get("SharingGroup").get("name").toString().replace("\"","");
        } catch (NullPointerException e) {
            LOG.debug("No sharing group distribution set for this event");
            result.put("", new ArrayList<>());
            return result; // returns empty map on sharing group absence
        }
        // Retrieving any synchronized Organisations in the Sharing Group even if it has no synchronization prefix.
        sharingGroupUuid = jsonNode.get(EVENT.toString()).get("SharingGroup").get("uuid").toString().replace("\"","");
        // Get any organisations found in the sharing group node.
        ArrayNode sGroupOrgs = (ArrayNode) jsonNode.get(EVENT.toString()).get("SharingGroup").get("SharingGroupOrg");
        // Catch the exception in case the sharing group has no organisations at all.
        boolean proceed = false;
        try {
            sGroupOrgs.isNull();
            proceed = true;
        } catch (NullPointerException e) {
            LOG.debug("No organisations found in the sharing group");
            return result;
        }
        if (proceed) {
            sGroupOrgs.forEach(o -> {
                // For each valid (synchronized) organisation, add its uuid in the list for use with sharing params.
                // Currently checking for both synchronization indications (name prefix OR organisation type)
                if (o.get("Organisation").get("name").toString().replace("\"","").startsWith(prefix)
                        || o.get("Organisation").get("type").toString().replace("\"","").equals(syncType)) {
                    syncedOrgsUuids.add(o.get("Organisation").get("uuid").toString().replace("\"",""));
                }
            });
            result.put("org", syncedOrgsUuids);
            return result; // When Sharing Group is valid, method returns its valid Organisations UUIDs along with a key indication.
        }
        result.put("sg", Arrays.asList(sharingGroupUuid));
        return result; // When Sharing Group is valid but has no Organisations, method returns its UUID along with a key indication.
    }

    private JsonNode updateTimestamp(JsonNode rootNode) {
        ReadContext ctx = JsonPath.using(configuration).parse(rootNode);
        List<String> timestampPaths = ctx.read("$..timestamp", List.class);
        Long timestamp = Instant.now().getEpochSecond() - 1;
        for (String path : timestampPaths){
            rootNode = JsonPath.using(configuration).parse(rootNode).set(path, String.valueOf(timestamp)).json();
        }
        return rootNode;
    }
}
