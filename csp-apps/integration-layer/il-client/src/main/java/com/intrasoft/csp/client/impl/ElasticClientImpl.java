package com.intrasoft.csp.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.client.ElasticClient;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.elastic.ElasticData;
import com.intrasoft.csp.commons.model.elastic.ElasticSearchRequest;
import com.intrasoft.csp.commons.model.elastic.ElasticSearchResponse;
import com.intrasoft.csp.commons.model.elastic.query.Bool;
import com.intrasoft.csp.commons.model.elastic.query.Match;
import com.intrasoft.csp.commons.model.elastic.query.Must;
import com.intrasoft.csp.commons.model.elastic.query.Query;
import com.intrasoft.csp.commons.model.elastic.search.Hit;
import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;
import com.intrasoft.csp.server.processors.ElasticProcessor;
import com.intrasoft.csp.server.service.CamelRestService;
import org.apache.camel.component.http.HttpMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ElasticClientImpl implements ElasticClient {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticProcessor.class);

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

        String response = camelRestService.send(this.getElasticURI() + "/" + dataType.toString().toLowerCase() + "/_search?pretty&_source=false", elasticSearchRequest, HttpMethods.POST.name());
        LOG.info("Elastic - ES Search response: " + response);

        if(response == null){
            //TODO: What do we want here
            LOG.info("Response from ES is null");
//            throw new CspBusinessException("No response from Elastic (null). Processor will fail and should send message to DeadLetterQ");
        }

        ElasticSearchResponse elasticSearchResponse = new ObjectMapper().readValue(response, ElasticSearchResponse.class);
        if (elasticSearchResponse.getHits().getTotal() == 0) return  false;
        else return true;
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
