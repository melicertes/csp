package com.intrasoft.csp.misp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.intrasoft.csp.client.CspClient;
import com.intrasoft.csp.client.ElasticClient;
import com.intrasoft.csp.commons.model.DataParams;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.SharingParams;
import com.intrasoft.csp.misp.service.EmitterDataHandler;
import com.intrasoft.csp.server.service.CamelRestService;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

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

    @Value("${server.name}")
    String cspId;

    @Autowired
    CspClient cspClient;

    @Value("${elastic.protocol}")
    String elasticProtocol;
    @Value("${elastic.host}")
    String elasticHost;
    @Value("${elastic.port}")
    String elasticPort;
    @Value("${elastic.path}")
    String elasticPath;

    @Autowired
    CamelRestService camelRestService;

    @Autowired
    ElasticClient elasticClient;

    @Override
    public void handleMispData(Object object) throws IOException {
        final Logger LOG = LoggerFactory.getLogger(EmitterDataHandlerImpl.class);

        JsonNode jsonNode = (JsonNode) object;

        String uuid = "";
        try{
            uuid = jsonNode.get("Event").get("uuid").toString();
        }
        catch (JSONException e){

        }

        DataParams dataParams = new DataParams();
         /** @TODO get it from application.properties: FIXED
          * */
        dataParams.setCspId(cspId);
         /** @TODO find enum from IL: ENUM NOT AVAILABLE
          * */
        dataParams.setApplicationId("misp");
        dataParams.setRecordId(uuid);
        dataParams.setDateTime(new DateTime());
         /** @TODO origing fields
          * the originids should stay the same (read from confluence)
          * check local mapping table, if not found use our own values*/
        dataParams.setOriginCspId("LOCAL-CERT");
        dataParams.setOriginApplicationId("misp");
        dataParams.setOriginRecordId(uuid);
        /** @FIXME setUrl: FIXED
         * get base url from application.properties
         * how does the url update from emitter of source to adapter of destination*/
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
        integrationData.setDataObject(jsonNode);

        integrationData.setDataType(IntegrationDataType.EVENT);


        LOG.info("Integration data: " + integrationData.toString());

        /** @TODO how to identify if it is post or put
         * should search in ES to see if this uuid exists
         * query the index based on datatype (event/threat), if found send put else send post */


        if (elasticClient.objectExists(integrationData)){
            cspClient.updateIntegrationData(integrationData);
        }
        else {
            cspClient.postIntegrationData(integrationData);
        }
    }


}
