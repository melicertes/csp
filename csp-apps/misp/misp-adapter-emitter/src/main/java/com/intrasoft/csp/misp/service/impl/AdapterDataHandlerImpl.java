package com.intrasoft.csp.misp.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.libraries.restclient.exceptions.StatusCodeException;
import com.intrasoft.csp.misp.client.MispAppClient;
import com.intrasoft.csp.misp.commons.config.MispContextUrl;
import com.intrasoft.csp.misp.domain.model.Origin;
import com.intrasoft.csp.misp.domain.service.impl.OriginServiceImpl;
import com.intrasoft.csp.misp.service.AdapterDataHandler;
import com.intrasoft.csp.misp.service.EmitterDataHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AdapterDataHandlerImpl implements AdapterDataHandler{

    @Autowired
    OriginServiceImpl originService;

    @Autowired
    @Qualifier("MispAppClient")
    MispAppClient mispAppClient;

    @Autowired
    EmitterDataHandler emitterDataHandler;


    @Override
    public ResponseEntity<String> handleIntegrationData(IntegrationData integrationData, String requestMethod) {

        final Logger LOG = LoggerFactory.getLogger(AdapterDataHandlerImpl.class);

        LOG.info(integrationData.getDataObject().toString());
        String uuid = new JSONObject(integrationData.getDataObject()).getJSONObject("Event").getString("uuid");
        JsonNode jsonNode = null;
        jsonNode = new ObjectMapper().convertValue(integrationData.getDataObject(), JsonNode.class);
        LOG.info(jsonNode.toString());
        try {
            LOG.info(new ObjectMapper().writeValueAsString(integrationData.getDataObject()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        List<Origin> origins = originService.findByRecordUuid(uuid);
        if (origins.isEmpty()){
            Origin origin = new Origin();
            origin.setOriginApplicationId(integrationData.getDataParams().getOriginApplicationId());
            origin.setOriginCspId(integrationData.getDataParams().getOriginCspId());
            origin.setOriginRecordId(integrationData.getDataParams().getOriginRecordId());
            origin.setApplicationId(integrationData.getDataParams().getApplicationId());
            origin.setCspId(integrationData.getDataParams().getCspId());
            origin.setRecordId(integrationData.getDataParams().getRecordId());
        }
        else {
            LOG.debug("Origin params already found in table");
        }

        integrationData.getSharingParams().setToShare(false);

        LOG.info("requestMethod: " + requestMethod);
        if (requestMethod.equals("DELETE")){
            LOG.info("Delete event with uuid: " + uuid);
            mispAppClient.deleteMispEvent(uuid);
        }
        else {
            try {
                LOG.info(integrationData.getDataObject().toString());
                ResponseEntity<String> responseEntity = mispAppClient.addMispEvent(jsonNode.toString());
                LOG.info(responseEntity.toString());
            }
            catch (StatusCodeException e){
                LOG.error(e.getMessage());
                if (!e.getHttpHeaders().get("location").isEmpty()){
                    String location = e.getHttpHeaders().get("location").get(0);
                    LOG.info("Event already exists at: " + location);
                    jsonNode = ((ObjectNode) jsonNode.get("Event")).put("timestamp", String.valueOf(Instant.now().getEpochSecond() + 1));
                    LOG.info(jsonNode.toString());
                    ResponseEntity<String> responseEntity = mispAppClient.updateMispEvent(location, jsonNode.toString());
                    LOG.info(responseEntity.toString());
                }
            }
        }

        /**
         * issue: SXCSP-339
         * Implement reemition flow
         */
        emitterDataHandler.handleReemittionMispData(integrationData, MispContextUrl.MispEntity.EVENT, false, true);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
