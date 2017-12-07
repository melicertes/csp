package com.intrasoft.csp.client.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.client.ElasticClient;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.elastic.ElasticSearchRequest;
import com.intrasoft.csp.commons.model.elastic.ElasticSearchResponse;
import com.intrasoft.csp.commons.model.elastic.query.Bool;
import com.intrasoft.csp.commons.model.elastic.query.Match;
import com.intrasoft.csp.commons.model.elastic.query.Must;
import com.intrasoft.csp.commons.model.elastic.query.Query;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ElasticClientImpl implements ElasticClient {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticClientImpl.class);

    @Value("${elastic.protocol}")
    String elasticProtocol;
    @Value("${elastic.host}")
    String elasticHost;
    @Value("${elastic.port}")
    String elasticPort;
    @Value("${elastic.path}")
    String elasticPath;

    @Autowired
    @Qualifier("ElasticRestTemplate")
    RetryRestTemplate retryRestTemplate;

    String context;

    @Override
    public void setProtocolHostPort(String protocol, String host, String port) {
        context = protocol+"://"+host+":"+port;
    }

    @Override
    public String getContext() {
        return context;
    }

    @Override
    public boolean objectExists(IntegrationData integrationData) throws IOException {
        ElasticSearchRequest elasticSearchRequest = this.getElasticSearchRequest(integrationData);
        IntegrationDataType dataType = integrationData.getDataType();

//        String response = camelRestService.send(this.getElasticURI() + "/" + dataType.toString().toLowerCase() + "/_search?pretty&_source=false", elasticSearchRequest, HttpMethods.POST.name());
        LOG.info(this.getElasticURI() + dataType.toString().toLowerCase() + "/_search?pretty&_source=false");
        LOG.info(elasticSearchRequest.toString());
        ResponseEntity<String> response = retryRestTemplate.postForEntity(this.getElasticURI() + "/" + dataType.toString().toLowerCase() + "/_search?pretty&_source=false", elasticSearchRequest,String.class);
        LOG.info("Elastic - ES Search response: " + response);

        if(response == null){
            //TODO: What do we want here
            LOG.info("Response from ES is null");
//            throw new CspBusinessException("No response from Elastic (null). Processor will fail and should send message to DeadLetterQ");
        }

        ElasticSearchResponse elasticSearchResponse = new ObjectMapper().readValue(response.getBody(), ElasticSearchResponse.class);
        if (elasticSearchResponse.getHits().getTotal() == 0) return  false;
        else return true;
    }

    @Override
    public JsonNode getESobject(IntegrationData integrationData) throws IOException {
        ElasticSearchRequest elasticSearchRequest = this.getElasticSearchRequest(integrationData);
        IntegrationDataType dataType = integrationData.getDataType();

        ResponseEntity<String> response = retryRestTemplate.postForEntity(this.getElasticURI() + "/" + dataType.toString().toLowerCase() + "/_search?pretty&_source=false", elasticSearchRequest,String.class);

        if(response == null){
            return null;
        }

        ElasticSearchResponse elasticSearchResponse = new ObjectMapper().readValue(response.getBody(), ElasticSearchResponse.class);
        if (elasticSearchResponse.getHits().getTotal() != 0) {
            String esId = elasticSearchResponse.getHits().getHits().get(0).getId();

            //http://csp0.dangerduck.gr:9200/cspdata/event/_search?q=_id:AV_WK4S8ZikppFWMb1IJ
            ResponseEntity<String> o = retryRestTemplate.getForEntity(this.getElasticURI() + "/" + dataType.toString().toLowerCase() + "/_search?q=_id:" + esId, String.class);

            JsonNode jsonNode = new ObjectMapper().readValue(o.getBody(), JsonNode.class);
            for (JsonNode jn : jsonNode.get("hits").get("hits")){
                return jn.get("_source");
            }
        }

        return null;
    }

    @Override
    public JsonNode getESobjectFromOrigin(IntegrationData integrationData) throws IOException {
        ElasticSearchRequest elasticSearchRequest = this.getElasticOriginSearchRequest(integrationData);
        IntegrationDataType dataType = integrationData.getDataType();

        ResponseEntity<String> response = retryRestTemplate.postForEntity(this.getElasticURI() + "/" + dataType.toString().toLowerCase() + "/_search?pretty&_source=false", elasticSearchRequest,String.class);

        if(response == null){
            return null;
        }

        ElasticSearchResponse elasticSearchResponse = new ObjectMapper().readValue(response.getBody(), ElasticSearchResponse.class);
        if (elasticSearchResponse.getHits().getTotal() != 0) {
            String esId = elasticSearchResponse.getHits().getHits().get(0).getId();

            //http://csp0.dangerduck.gr:9200/cspdata/event/_search?q=_id:AV_WK4S8ZikppFWMb1IJ
            ResponseEntity<String> o = retryRestTemplate.getForEntity(this.getElasticURI() + "/" + dataType.toString().toLowerCase() + "/_search?q=_id:" + esId, String.class);

            JsonNode jsonNode = new ObjectMapper().readValue(o.getBody(), JsonNode.class);
            for (JsonNode jn : jsonNode.get("hits").get("hits")){
                return jn.get("_source");
            }
        }

        return null;
    }

    private String getElasticURI() {
        return elasticProtocol + "://" + elasticHost + ":" + elasticPort + "/" + elasticPath;
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

    private Query getElasticOriginQuery(IntegrationData integrationData) {
        Match t1 = new Match();
        Match t2 = new Match();
        Match t3 = new Match();

        t1.setOriginRecordId(integrationData.getDataParams().getOriginRecordId());
        t2.setOriginCspId(integrationData.getDataParams().getOriginCspId());
        t3.setOriginApplicationId(integrationData.getDataParams().getOriginApplicationId());

        Must m1 = new Must();
        Must m2 = new Must();
        Must m3 = new Must();

        m1.setMatch(t1);
        m2.setMatch(t2);
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

    private ElasticSearchRequest getElasticOriginSearchRequest(IntegrationData integrationData) {
        //create search transaction object
        List<String> fields = new ArrayList<>();
        fields.add("_id");

        ElasticSearchRequest elasticSearchRequest = new ElasticSearchRequest();
        elasticSearchRequest.setQuery(this.getElasticOriginQuery(integrationData));
        //elasticSearchRequest.setFields(fields);

        return elasticSearchRequest;
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

}
