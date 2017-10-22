package com.intrasoft.csp.misp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.intrasoft.csp.commons.routes.ContextUrl.DSL_INTEGRATION_DATA;
import static com.intrasoft.csp.misp.commons.utils.JsonObjectHandler.readField;

@Service
public class EmitterDataHandlerImpl implements EmitterDataHandler {

    @Value("${misp.app.protocol}")
    String protocol;

    @Value("${misp.app.host}")
    String host;

    @Value("${misp.app.port}")
    String port;

    @Value("${misp.app.authorization.key}")
    String authorizationKey;

    @Value("${misp.app.events.path}")
    String eventsPath;

    @Override
    public void handleMispData(String content) throws IOException {
        final Logger LOG = LoggerFactory.getLogger(EmitterDataHandlerImpl.class);

        String uuid = "";
        try{
            uuid = readField( content, "uuid");
        }
        catch (JSONException e){

        }

        DataParams dataParams = new DataParams();
         /** @TODO get it from application.properties*/
        dataParams.setCspId("LOCAL-CERT");
         /** @TODO find enum from IL*/
        dataParams.setApplicationId("misp");
        dataParams.setRecordId(uuid);
        dataParams.setDateTime(new DateTime());
         /** @TODO origing fields
          * the originids should stay the same (read from confluence)
          * check local mapping table, if not found use our own values*/
        dataParams.setOriginCspId("LOCAL-CERT");
        dataParams.setOriginApplicationId("misp");
        dataParams.setOriginRecordId(uuid);
        /** @TODO setUrl
         * get base url from application.properties
         * bug in misp, uuid always returns 1st event, Kyr. should report it
         * how does the url update from emiter of source to adapter of destination*/
        dataParams.setUrl(host + ":" + port + "/events/" + uuid);

        SharingParams sharingParams = new SharingParams();
        sharingParams.setIsExternal(false);
         /** @TODO setToShare
          * When the adapter receives data from an external CSP (the “isExternal” flag is set to TRUE)
          * the operation should trigger an emitter response. The emitter should emit this record (for indexing) and
          * set the “toShare” flag to FALSE (rest on conluence https://confluence.sastix.com/display/SXCSP/Integration+Layer+Flows).*/
        sharingParams.setToShare(true);
        /** @TODO setTcId and setTeamID
         * should be harvested from dataobject when we support for the user to specify tc or team as recipient
         * find out how to differentiate our custom shared groups from the normal ones
         * use custom sharing groups uuids as tcid, use custom organizations(?) uuids as team id.
         * harvest only from the dataobject part which dictates which organization or sharing group should get this event*/
        sharingParams.setTcId("\"\"");
        sharingParams.setTeamId("\"\"");





        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataParams(dataParams);
        integrationData.setSharingParams(sharingParams);
        integrationData.setDataObject(content);

        integrationData.setDataType(IntegrationDataType.EVENT);


        LOG.info("Integration data: " + integrationData.toString());

        /** @TODO how to identify if it is post or put
         * shoud search in ES to see if this uuid exists
         * query the index based on datatype (event/threat), if found send put else send post */

//        CspClient cspClient = new CspClientImpl();
//        cspClient.postIntegrationData(integrationData, DSL_INTEGRATION_DATA);

//        MispAppClient mispAppClient = new MispAppClientImpl();
//        mispAppClient.setProtocolHostPortHeaders(protocol, host, port, eventsPath, authorizationKey);
//        mispAppClient.updateMispEvent(uuid, new ObjectMapper().writeValueAsString(integrationData.getDataObject()));

    }
}
