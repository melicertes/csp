package com.intrasoft.csp.misp.service.impl;

import com.intrasoft.csp.client.CspClient;
import com.intrasoft.csp.client.impl.CspClientImpl;
import com.intrasoft.csp.commons.model.DataParams;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.SharingParams;
import com.intrasoft.csp.misp.client.MispAppClient;
import com.intrasoft.csp.misp.client.impl.MispAppClientImpl;
import com.intrasoft.csp.misp.service.EmitterDataHandler;
import com.intrasoft.csp.misp.service.EmitterSubscriber;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.intrasoft.csp.commons.routes.ContextUrl.DSL_INTEGRATION_DATA;

@Service
public class EmitterDataHandlerImpl implements EmitterDataHandler {
    @Override
    public void handleMispData(String content) {
        final Logger LOG = LoggerFactory.getLogger(EmitterDataHandlerImpl.class);

        String uuid = "";
        try{
            uuid = new JSONObject(content).getString("uuid");
        }
        catch (JSONException e){

        }

        DataParams dataParams = new DataParams();
        // TODO get it from application.properties
        dataParams.setCspId("LOCAL-CERT");
        // TODO find enum from IL
        dataParams.setApplicationId("misp");
        // TODO record UUID
        dataParams.setRecordId(uuid);
        // TODO emition datetime
        dataParams.setDateTime(new DateTime());
        // TODO the originids should stay the same (read from confluence)
        // check local mapping table, if not found use our own values
        dataParams.setOriginCspId("LOCAL-CERT");
        dataParams.setOriginApplicationId("misp");
        dataParams.setOriginRecordId(uuid);
        // TODO get base url from application.properties
        // bug in misp, uuid always returns 1st event, Kyr. should report it
        dataParams.setUrl("localhost:8181/events/view/" + uuid);

        SharingParams sharingParams = new SharingParams();
        sharingParams.setIsExternal(false);
        // TODO When the adapter receives data from an external CSP (the “isExternal” flag is set to TRUE)
        // the operation should trigger an emitter response. The emitter should emit this record (for indexing) and
        // set the “toShare” flag to FALSE (rest on conluence https://confluence.sastix.com/display/SXCSP/Integration+Layer+Flows).
        sharingParams.setToShare(true);
        // TODO should be harvested from dataobject when we support for the user to specify tc or team as recipient
        // find out how to differentiate our custom shared groups from the normal ones
        // use custom sharing groups uuids as tcid, use custom organizations(?) uuids as team id.
        // harvest only from the dataobject part which dictates which organization or sharing group should get this event
        sharingParams.setTcId("\"\"");
        sharingParams.setTeamId("\"\"");





        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataParams(dataParams);
        integrationData.setSharingParams(sharingParams);
        integrationData.setDataObject(content);

        integrationData.setDataType(IntegrationDataType.EVENT);


        LOG.info("Integration data: " + integrationData.toString());

        // TODO how to identify if it is post or put
        // shoud search in ES to see if this uuid exists
        // query the index based on datatype (event/threat), if found send put else send post

//        CspClient cspClient = new CspClientImpl();
//        cspClient.postIntegrationData(integrationData, DSL_INTEGRATION_DATA);

//        MispAppClient mispAppClient = new MispAppClientImpl();
//        mispAppClient.addMispEvent(content);

    }
}
