package com.intrasoft.csp.client.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.client.ElasticClient;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.elastic.ElasticSearchRequest;
import com.intrasoft.csp.commons.model.elastic.ElasticSearchResponse;
import com.intrasoft.csp.commons.model.elastic.query.Bool;
import com.intrasoft.csp.commons.model.elastic.query.Term;
import com.intrasoft.csp.commons.model.elastic.query.Filter;
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
        LOG.debug(this.getElasticURI() + dataType.toString().toLowerCase() + "/_search?pretty&_source=false");
        LOG.debug(elasticSearchRequest.toString());
        ResponseEntity<String> response = retryRestTemplate.postForEntity(this.getElasticURI() + "/" + dataType.toString().toLowerCase() + "/_search?pretty&_source=false", elasticSearchRequest,String.class);
        LOG.debug("Elastic - ES Search response: " + response);

        if(response == null){
            //TODO: What do we want here
            LOG.debug("Response from ES is null");
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

        Term t1 = new Term();
        t1.setRecordId(integrationData.getDataParams().getRecordId());

        Term t2 = new Term();
        t2.setCspId(integrationData.getDataParams().getCspId());

        Term t3 = new Term();
        t3.setApplicationId(integrationData.getDataParams().getApplicationId());

        Filter m1 = new Filter();
        m1.setTerm(t1);
        Filter m2 = new Filter();
        m2.setTerm(t2);
        Filter m3 = new Filter();
        m3.setTerm(t3);

        ArrayList<Filter> filter = new ArrayList<>();
        filter.add(m1);
        filter.add(m2);
        filter.add(m3);

        Bool bool = new Bool();
        bool.setFilter(filter);

        Query query = new Query();
        query.setBool(bool);

        return query;
    }

    private Query getElasticOriginQuery(IntegrationData integrationData) {
        Term t1 = new Term();
        Term t2 = new Term();
        Term t3 = new Term();

        t1.setOriginRecordId(integrationData.getDataParams().getOriginRecordId());
        t2.setOriginCspId(integrationData.getDataParams().getOriginCspId());
        t3.setOriginApplicationId(integrationData.getDataParams().getOriginApplicationId());

        Filter m1 = new Filter();
        Filter m2 = new Filter();
        Filter m3 = new Filter();

        m1.setTerm(t1);
        m2.setTerm(t2);
        m3.setTerm(t3);

        ArrayList<Filter> filter = new ArrayList<>();
        filter.add(m1);
        filter.add(m2);
        filter.add(m3);

        Bool bool = new Bool();
        bool.setFilter(filter);

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
