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
import com.intrasoft.csp.misp.domain.model.Origin;
import com.intrasoft.csp.misp.domain.service.impl.OriginServiceImpl;
import com.intrasoft.csp.misp.service.AdapterDataHandler;
import com.intrasoft.csp.misp.service.EmitterDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
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


    @Override
    public ResponseEntity<String> handleIntegrationData(IntegrationData integrationData, String requestMethod) {

        LOG.info(integrationData.getDataObject().toString());
        String uuid = integrationData.getDataParams().getOriginRecordId();

        JsonNode jsonNode = null;
        jsonNode = new ObjectMapper().convertValue(integrationData.getDataObject(), JsonNode.class);
        LOG.info(jsonNode.toString());
        try {
            LOG.info(new ObjectMapper().writeValueAsString(integrationData.getDataObject()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        List<Origin> origins = originService.findByOriginRecordId(uuid);
        if (origins.isEmpty()){
            LOG.info("Entry for " + uuid + " not found.");
            Origin origin = new Origin();
            origin.setOriginCspId(integrationData.getDataParams().getOriginCspId());
            origin.setOriginApplicationId(integrationData.getDataParams().getOriginApplicationId());
            origin.setOriginRecordId(integrationData.getDataParams().getOriginRecordId());
            origin.setCspId(cspId);
            origin.setApplicationId("misp");
            origin.setRecordId(integrationData.getDataParams().getOriginRecordId());
            Origin org = originService.saveOrUpdate(origin);
            LOG.info("Origin inserted: " + org);
        }
        else {
            LOG.debug("Origin params already found in table");
        }


        /*
        Search and handle RTIR objects
         */
        integrationData = handleRTIR(integrationData);
        integrationData = handleVulnerability(integrationData);
        /*
        Remake jsonNode
         */
        jsonNode = new ObjectMapper().convertValue(integrationData.getDataObject(), JsonNode.class);


        integrationData.getSharingParams().setToShare(false);




        LOG.info("requestMethod: " + requestMethod);
        if (requestMethod.equals("DELETE")) {
            LOG.info("Delete event with uuid: " + uuid);
            mispAppClient.deleteMispEvent(uuid);
        } else {
            try {
                LOG.info(integrationData.getDataObject().toString());

                ResponseEntity<String> responseEntity = mispAppClient.addMispEvent(jsonNode.toString());
                status = responseEntity.getStatusCode();
                LOG.info(responseEntity.toString());
            } catch (StatusCodeException e) {
                LOG.error(e.getMessage());
                if (!e.getHttpHeaders().get("location").isEmpty()) {
                    String location = e.getHttpHeaders().get("location").get(0);
                    LOG.info("" + location);
                    jsonNode = ((ObjectNode) jsonNode.get("Event")).put("timestamp", String.valueOf(Instant.now().getEpochSecond() + 1));
                    LOG.info(jsonNode.toString());
                    ResponseEntity<String> responseEntity = mispAppClient.updateMispEvent(location, jsonNode.toString());
                    status = responseEntity.getStatusCode();
                    LOG.info(responseEntity.toString());
                }
            }
        }

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
                //LOG.info(jn.toString());
                //LOG.info(jn.get("name").toString());
                //LOG.info(MispContextUrl.RTIREntity.RTIR_NAME.toString());
                if (jn.get("name").toString().toLowerCase().equals(MispContextUrl.RTIREntity.RTIR_NAME.toString().toLowerCase())){
                    for (JsonNode ja : jn.get("Attribute")){
                        //LOG.info(ja.toString());
                        //LOG.info(ja.get("object_relation").toString().toLowerCase());
                        //LOG.info(MispContextUrl.RTIREntity.TITLE_RELATION.toString().toLowerCase());
                        if (ja.get("object_relation").toString().toLowerCase().equals(MispContextUrl.RTIREntity.MAP_CSP_FIELDS.toString().toLowerCase()) &&
                                ja.get("value").toString().toLowerCase().equals(MispContextUrl.RTIREntity.MAP_CSP_URL_VALUE.toString().toLowerCase()) &&
                                url.length() == 0) {
                            url = ja.get("comment").toString();
                            //LOG.info("============RTIR title: " + title);
                        }
                        if (ja.get("object_relation").toString().toLowerCase().equals(MispContextUrl.RTIREntity.MAP_CSP_FIELDS.toString().toLowerCase()) &&
                                ja.get("value").toString().toLowerCase().equals(MispContextUrl.RTIREntity.MAP_ORIGIN_CSP_ID_VALUE.toString().toLowerCase()) &&
                                originCspId.length() == 0) {
                            originCspId = ja.get("comment").toString();
                            //LOG.info("============RTIR url: " + url);
                        }
                        if (ja.get("object_relation").toString().toLowerCase().equals(MispContextUrl.RTIREntity.MAP_CSP_FIELDS.toString().toLowerCase()) &&
                                ja.get("value").toString().toLowerCase().equals(MispContextUrl.RTIREntity.MAP_ORIGIN_RECORD_ID_VALUE.toString().toLowerCase()) &&
                                originRecordId.length() == 0) {
                            originRecordId = ja.get("comment").toString();
                            //LOG.info("============RTIR url: " + url);
                        }
                        if (ja.get("object_relation").toString().toLowerCase().equals(MispContextUrl.RTIREntity.MAP_TITLE.toString().toLowerCase()) &&
                                title.length() == 0) {
                            title = ja.get("value").toString();
                        }
                        if (ja.get("object_relation").toString().toLowerCase().equals(MispContextUrl.RTIREntity.MAP_TICKET_NO.toString().toLowerCase()) &&
                                recordId.length() == 0) {
                            recordId = ja.get("value").toString();
                        }
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
                        LOG.info("FOUND TRUE");
                        newURL = esObject.get("dataParams").get("url").toString();
                        newTickerNumber = esObject.get("dataParams").get("recordId").toString();
                        newURL = newURL.replaceAll("\"", "");
                        newTickerNumber = newTickerNumber.replaceAll("\"", "");
                    }
                    else {
                        LOG.info("NOT FOUND");
                    }
                    //replace
                    for (JsonNode ja : jn.get("Attribute")){
                        if (ja.get("object_relation").toString().toLowerCase().equals(MispContextUrl.RTIREntity.MAP_CSP_FIELDS.toString().toLowerCase()) &&
                                ja.get("value").toString().toLowerCase().equals(MispContextUrl.RTIREntity.MAP_CSP_URL_VALUE.toString().toLowerCase()) ) {
                            ((ObjectNode)ja).put("comment",  newURL);
                        }
                        if (ja.get("object_relation").toString().toLowerCase().equals(MispContextUrl.RTIREntity.MAP_TICKET_NO.toString().toLowerCase()) ) {
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

        //update IntegrationData
        modifiedIlData.setDataObject(jsonNode);


        title = title.replaceAll("\"", "");
        url = url.replaceAll("\"", "");
        recordId = recordId.replaceAll("\"", "");
        originCspId = originCspId.replaceAll("\"", "");
        originRecordId = originRecordId.replaceAll("\"", "");
        LOG.info("RTIR title: " + title);
        LOG.info("RTIR url: " + url);
        LOG.info("RTIR recordId: " + recordId);
        LOG.info("RTIR originCspId: " + originCspId);
        LOG.info("RTIR originRecordId: " + originRecordId);
        LOG.info("MODIFIED IL DATA: " + modifiedIlData.toString());

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
                if (jn.get("name").toString().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.VULNERABILITY_NAME.toString().toLowerCase())){
                    for (JsonNode ja : jn.get("Attribute")){
                        if (ja.get("object_relation").toString().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_CSP_FIELDS.toString().toLowerCase()) &&
                                ja.get("value").toString().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_CSP_URL_VALUE.toString().toLowerCase()) &&
                                url.length() == 0) {
                            url = ja.get("comment").toString();
                            url = url.replaceAll("\"", "");
                            LOG.info("============VULNERABILITY url: " + url);
                        }
                        if (ja.get("object_relation").toString().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_CSP_FIELDS.toString().toLowerCase()) &&
                                ja.get("value").toString().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_ORIGIN_CSP_ID_VALUE.toString().toLowerCase()) &&
                                originCspId.length() == 0) {
                            originCspId = ja.get("comment").toString();
                            originCspId = originCspId.replaceAll("\"", "");
                            LOG.info("============VULNERABILITY originCspId: " + originCspId);
                        }
                        if (ja.get("object_relation").toString().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_CSP_FIELDS.toString().toLowerCase()) &&
                                ja.get("value").toString().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_ORIGIN_RECORD_ID_VALUE.toString().toLowerCase()) &&
                                originRecordId.length() == 0) {
                            originRecordId = ja.get("comment").toString();
                            originRecordId = originRecordId.replaceAll("\"", "");
                            LOG.info("============VULNERABILITY originRecordId: " + originRecordId);
                        }
                        if (ja.get("object_relation").toString().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_TITLE_RELATION.toString().toLowerCase()) &&
                                ja.get("category").toString().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_TITLE_CATEGORY.toString().toLowerCase()) &&
                                title.length() == 0) {
                            title = ja.get("value").toString();
                            title = title.replaceAll("\"", "");
                            LOG.info("============VULNERABILITY title: " + title);
                        }
                        if (ja.get("object_relation").toString().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_RECORD_RELATION.toString().toLowerCase()) &&
                                ja.get("category").toString().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_RECORD_CATEGORY.toString().toLowerCase()) &&
                                recordId.length() == 0) {
                            recordId = ja.get("value").toString();
                            recordId = recordId.replaceAll("\"", "");
                            LOG.info("============VULNERABILITY recordId: " + recordId);
                        }
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
                        LOG.info("FOUND TRUE");
                        newURL = esObject.get("dataParams").get("url").toString();
                        newTickerNumber = esObject.get("dataParams").get("recordId").toString();
                        newURL = newURL.replaceAll("\"", "");
                        newTickerNumber = newTickerNumber.replaceAll("\"", "");
                    }
                    else {
                        LOG.info("NOT FOUND");
                    }
                    //replace
                    for (JsonNode ja : jn.get("Attribute")){
                        if (ja.get("object_relation").toString().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_CSP_FIELDS.toString().toLowerCase()) &&
                                ja.get("value").toString().toLowerCase().equals(MispContextUrl.VULNERABILITYEntity.MAP_CSP_URL_VALUE.toString().toLowerCase()) ) {
                            ((ObjectNode)ja).put("comment",  newURL);
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
                LOG.info("VULNERABILITY new ID: " + newTickerNumber);
            }
        }

        //update IntegrationData
        modifiedIlData.setDataObject(jsonNode);

        return modifiedIlData;
    }
}
