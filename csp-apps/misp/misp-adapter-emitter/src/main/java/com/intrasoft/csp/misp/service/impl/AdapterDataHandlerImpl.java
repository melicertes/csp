package com.intrasoft.csp.misp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.client.ElasticClient;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.misp.client.MispAppClient;
import com.intrasoft.csp.misp.client.MispClient;
import com.intrasoft.csp.misp.client.impl.MispAppClientImpl;
import com.intrasoft.csp.misp.commons.config.MispContextUrl;
import com.intrasoft.csp.misp.domain.model.Origin;
import com.intrasoft.csp.misp.domain.service.impl.OriginServiceImpl;
import com.intrasoft.csp.misp.service.AdapterDataHandler;
import com.sun.org.apache.xpath.internal.axes.RTFIterator;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.intrasoft.csp.misp.commons.config.MispContextUrl.MispEntity.EVENT;

@Service
public class AdapterDataHandlerImpl implements AdapterDataHandler{

    @Autowired
    OriginServiceImpl originService;

    @Autowired
    ElasticClient elasticClient;

    @Autowired
    @Qualifier("MispClient")
    MispClient mispClient;

    @Autowired
    @Qualifier("MispAppClient")
    MispAppClient mispAppClient;


    @Override
    public ResponseEntity<String> handleIntegrationData(IntegrationData integrationData, String requestMethod) {

        final Logger LOG = LoggerFactory.getLogger(AdapterDataHandlerImpl.class);


        // TODO deduplication, nothing needed, push the event, if the event exists we should a redirect, read the API
        // TODO reemission, if external == true then toShare = false, two options
        // 1. Configure misp to publish automatically to zmq, adapter adds event to misp and adds uuid to a list.
        //    emitter checks the list, if exists toShare=false and remove from in memory list
        // 2. Bypass the MISP app and adapter sends dataObjecto emitter with a flag toShare=false (EmitterDataHandler)


        String uuid = "";
        JsonNode jsonNode = new ObjectMapper().convertValue(integrationData.getDataObject(), JsonNode.class);
        LOG.info(jsonNode.toString());
        LOG.info(jsonNode.get("Event").toString());
        LOG.info(jsonNode.get("Event").get("id").toString());
        uuid = jsonNode.get("Event").get("uuid").toString();

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


        /**
         * Issue: SXCSP-340, SXCSP-341
         * Reference resolving in RTIR objects
         */
        // TODO reference resolving
        String title = "";
        String url = "";
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
                        if (ja.get("object_relation").toString().toLowerCase().equals(MispContextUrl.RTIREntity.TITLE_RELATION.toString().toLowerCase()) &&
                                ja.get("category").toString().toLowerCase().equals(MispContextUrl.RTIREntity.TITLE_CATEGORY.toString().toLowerCase()) &&
                                title.length() == 0) {
                            title = ja.get("value").toString();
                            //LOG.info("============RTIR title: " + title);
                        }
                        if (ja.get("object_relation").toString().toLowerCase().equals(MispContextUrl.RTIREntity.URL_RELATION.toString().toLowerCase()) &&
                                ja.get("category").toString().toLowerCase().equals(MispContextUrl.RTIREntity.URL_CATEGORY.toString().toLowerCase()) &&
                                url.length() == 0) {
                            url = ja.get("value").toString();
                            //LOG.info("============RTIR url: " + url);
                        }
                    }
                }
            }
        }
        title = title.replaceAll("\"", "");
        url = url.replaceAll("\"", "");
        LOG.info("RTIR title: " + title);
        LOG.info("RTIR url: " + url);

        /**
         * TODO:
         1. The adapter queries ES for the existence of an "incident" with the given origin cspid/appid/id
         2. If it finds such incidence, it rewrites the incidence url inside the RTIR object with the url that this incidence has in ES
         3. If not It deletes the url field (Which field? RTIR or dataParams)!!!!!
         4. It pushes the event with the rewriten RTIR fields to misp
         */
        try {
            JsonNode esObject = elasticClient.getESobject(integrationData);
            if (esObject != null) {
                LOG.info(esObject.toString());
                /**
                 * TODO: Actual string replace
                 */
            }
        }
        catch (Exception e){
            LOG.info(e.getMessage());
        }




//        MispAppClient mispAppClient = new MispAppClientImpl();
//        mispAppClient.setProtocolHostPortHeaders(protocol,host,port,authorizationKey);


        //TODO process and post integration data to MISP API
        //mispAppClient.addMispEvent((String) jsonNode.toString());

        /*
        if (requestMethod.equals("POST")){
            mispAppClient.addMispEvent((String) integrationData.getDataObject());
        }
        else if (requestMethod.equals("PUT")){
            try {
                LOG.info(jsonNode.toString());
                mispAppClient.updateMispEvent(uuid.replace("\"",""), jsonNode.toString());
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }
        else if (requestMethod.equals("DELETE")){
            // TODO id is not unique among the csps,needs care and proderm
            mispAppClient.deleteMispEvent(new JSONObject(integrationData.getDataObject()).getJSONObject("Event").getString("uuid"));
        }*/

        /**
         * issue: SXCSP-339
         * Implement reemition flow
         */
        integrationData.getSharingParams().setToShare(false);
        //mispClient.postIntegrationDataEmitter(integrationData);

//        uuidSet.add(uuid);
        return null;
    }
}
