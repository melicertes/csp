package com.intrasoft.csp.misp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.intrasoft.csp.client.CspClient;
import com.intrasoft.csp.commons.model.DataParams;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.SharingParams;
import com.intrasoft.csp.commons.model.elastic.ElasticSearchRequest;
import com.intrasoft.csp.commons.model.elastic.query.Bool;
import com.intrasoft.csp.commons.model.elastic.query.Match;
import com.intrasoft.csp.commons.model.elastic.query.Must;
import com.intrasoft.csp.commons.model.elastic.query.Query;
import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;
import com.intrasoft.csp.misp.service.EmitterDataHandler;
import com.intrasoft.csp.server.service.CamelRestService;
import org.apache.camel.component.http.HttpMethods;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        ElasticSearchRequest elasticSearchRequest = this.getElasticSearchRequest(integrationData);
        String response = camelRestService.send(this.getElasticURI() + "/" + integrationData.getDataType().toString().toLowerCase() + "/_search?pretty&_source=false", elasticSearchRequest, HttpMethods.POST.name());
        if(response == null){
            /** @TODO: Object not existing in ES, should propagate it as a POST */
        }
        else {
            /** @TODO: Object exists in ES, should propagate it as a PUT */
        }

        /** MispAppClient mispAppClient = new MispAppClientImpl();
        mispAppClient.setProtocolHostPortHeaders(protocol, host, port, authorizationKey);
        mispAppClient.updateMispEvent(uuid.replace("\"",""), new ObjectMapper().writeValueAsString(integrationData.getDataObject()));
         */

        cspClient.postIntegrationData(integrationData);
//        CspClient cspClient = new CspClientImpl();
//        LOG.info(cspClient.toString());
//        cspClient.postIntegrationData(integrationData);

    }

    private String getElasticURI() {
        return elasticProtocol + "://" + elasticHost + ":" + elasticPort + elasticPath;
    }

    private ElasticSearchRequest getElasticSearchRequest(IntegrationData integrationData) {
        //create search transaction object
        List<String> fields = new ArrayList<>();
        fields.add("_id");

        ElasticSearchRequest elasticSearchRequest = new ElasticSearchRequest();
        elasticSearchRequest.setQuery(this.getElasticQuery(integrationData));
        //elasticSearchRequest.setFields(fields);

        return elasticSearchRequest;
    }

    private Query getElasticQuery(IntegrationData integrationData) {

        Match t1 = new Match();
        t1.setRecordId(integrationData.getDataParams().getRecordId());

        Match t2 = new Match();
        t2.setCspId(integrationData.getDataParams().getCspId());

        Match t3 = new Match();
        t3.setApplicationId(integrationData.getDataParams().getApplicationId());

        Must m1 = new Must();
        m1.setMatch(t1);
        Must m2 = new Must();
        m2.setMatch(t2);
        Must m3 = new Must();
        m3.setMatch(t3);

        ArrayList<Must> must = new ArrayList<>();
        must.add(m1);
        must.add(m2);
        must.add(m3);

        Bool bool = new Bool();
        bool.setMust(must);

        Query query = new Query();
        query.setBool(bool);

        return query;
    }
}
