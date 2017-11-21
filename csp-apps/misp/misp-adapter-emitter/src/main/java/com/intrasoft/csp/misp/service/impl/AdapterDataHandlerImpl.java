package com.intrasoft.csp.misp.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.libraries.restclient.exceptions.StatusCodeException;
import com.intrasoft.csp.misp.client.MispAppClient;
import com.intrasoft.csp.misp.client.MispClient;
import com.intrasoft.csp.misp.client.impl.MispAppClientImpl;
import com.intrasoft.csp.misp.domain.model.Origin;
import com.intrasoft.csp.misp.domain.service.impl.OriginServiceImpl;
import com.intrasoft.csp.misp.service.AdapterDataHandler;
import org.json.JSONObject;
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
import java.util.List;

@Service
public class AdapterDataHandlerImpl implements AdapterDataHandler{

    @Autowired
    OriginServiceImpl originService;

    @Autowired
    @Qualifier("MispClient")
    MispClient mispClient;

    @Autowired
    @Qualifier("MispAppClient")
    MispAppClient mispAppClient;


    @Override
    public ResponseEntity<String> handleIntegrationData(IntegrationData integrationData, String requestMethod) {

        final Logger LOG = LoggerFactory.getLogger(AdapterDataHandlerImpl.class);
        //TODO process and post integration data to MISP API

        // TODO deduplication, nothing needed, push the event, if the event exists we should a redirect, read the API
        // TODO reemission, if external == true then toShare = false, two options
        // 1. Configure misp to publish automatically to zmq, adapter adds event to misp and adds uuid to a list.
        //    emitter checks the list, if exists toShare=false and remove from in memory list
        // 2. Bypass the MISP app and adapter sends dataObjecto emitter with a flag toShare=false (EmitterDataHandler)

        // TODO reference resolving

        LOG.info(integrationData.getDataObject().toString());
        String uuid = "";
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

        //////////////////////////////////////////////////////////////
        integrationData.getSharingParams().setToShare(false);
        mispClient.postIntegrationDataEmitter(integrationData);
        //////////////////////////////////////////////////////////////

        LOG.info("requestMethod: " + requestMethod);
        if (requestMethod.equals("DELETE")){
            // TODO id is not unique among the csps,needs care and proderm
            mispAppClient.deleteMispEvent(new JSONObject(integrationData.getDataObject()).getJSONObject("Event").getString("uuid"));
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
                    LOG.info(location);
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
        mispClient.postIntegrationDataEmitter(integrationData);

//        uuidSet.add(uuid);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
