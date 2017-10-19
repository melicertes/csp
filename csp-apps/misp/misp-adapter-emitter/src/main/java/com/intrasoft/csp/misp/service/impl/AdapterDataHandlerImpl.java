package com.intrasoft.csp.misp.service.impl;

import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.misp.client.MispAppClient;
import com.intrasoft.csp.misp.client.impl.MispAppClientImpl;
import com.intrasoft.csp.misp.service.AdapterDataHandler;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AdapterDataHandlerImpl implements AdapterDataHandler{
    @Override
    public ResponseEntity<String> handleIntegrationData(IntegrationData integrationData, String requestMethod) {

        //TODO process and post integration data to MISP API

        MispAppClient mispAppClient = new MispAppClientImpl();

        // TODO deduplication, nothing needed, push the event, if the event exists we should a redirect, read the API
        // TODO reemission, if external == true then toShare = false, two options
        // 1. Configure misp to publish automatically to zmq, adapter adds event to misp and adds uuid to a list.
        //    emitter checks the list, if exists toShare=false and remove from in memory list
        // 2. Bypass the MISP app and adapter sends dataObjecto emitter with a flag toShare=false (EmitterDataHandler)

        // TODO reference resolving

        if (requestMethod.equals("POST")){
            mispAppClient.addMispEvent((String) integrationData.getDataObject());
        }
        else if (requestMethod.equals("PUT")){
            mispAppClient.updateMispEvent((String) integrationData.getDataObject());
        }
        else if (requestMethod.equals("DELETE")){
            // TODO id is not unique among the csps,needs care and proderm
            mispAppClient.deleteMispEvent(new JSONObject(integrationData).getInt("id"));
        }

        return null;
    }
}
