package com.intrasoft.csp.misp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.misp.client.MispAppClient;
import com.intrasoft.csp.misp.client.MispClient;
import com.intrasoft.csp.misp.client.impl.MispAppClientImpl;
import com.intrasoft.csp.misp.domain.model.Origin;
import com.intrasoft.csp.misp.domain.service.impl.OriginServiceImpl;
import com.intrasoft.csp.misp.service.AdapterDataHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

@Service
public class AdapterDataHandlerImpl implements AdapterDataHandler{

    @Autowired
    OriginServiceImpl originService;

    @Autowired
    MispClient mispClient;

/*    @Autowired
    @Qualifier("uuidSet")
    HashSet<String> uuidSet;*/

    @Override
    public ResponseEntity<String> handleIntegrationData(IntegrationData integrationData, String requestMethod) {

        final Logger LOG = LoggerFactory.getLogger(AdapterDataHandlerImpl.class);
        //TODO process and post integration data to MISP API

        MispAppClient mispAppClient = new MispAppClientImpl();

        // TODO deduplication, nothing needed, push the event, if the event exists we should a redirect, read the API
        // TODO reemission, if external == true then toShare = false, two options
        // 1. Configure misp to publish automatically to zmq, adapter adds event to misp and adds uuid to a list.
        //    emitter checks the list, if exists toShare=false and remove from in memory list
        // 2. Bypass the MISP app and adapter sends dataObjecto emitter with a flag toShare=false (EmitterDataHandler)

        // TODO reference resolving

        JsonNode jsonNode = (JsonNode) integrationData.getDataObject();

        String uuid = "";
        try{
            uuid = jsonNode.get("Event").get("uuid").toString();
        }
        catch (JSONException e){

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

        if (requestMethod.equals("POST")){
            mispAppClient.addMispEvent((String) integrationData.getDataObject());
        }
        else if (requestMethod.equals("PUT")){
            try {
                mispAppClient.updateMispEvent(new JSONObject(integrationData.getDataObject()).getJSONObject("Event").getString("uuid"), (String) integrationData.getDataObject());
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }
        else if (requestMethod.equals("DELETE")){
            // TODO id is not unique among the csps,needs care and proderm
            mispAppClient.deleteMispEvent(new JSONObject(integrationData.getDataObject()).getJSONObject("Event").getString("uuid"));
        }

        /**
         * issue: SXCSP-339
         * Implement reemition flow
         */
        integrationData.getSharingParams().setToShare(false);
        mispClient.postIntegrationDataEmitter(integrationData);

//        uuidSet.add(uuid);
        return null;
    }
}
