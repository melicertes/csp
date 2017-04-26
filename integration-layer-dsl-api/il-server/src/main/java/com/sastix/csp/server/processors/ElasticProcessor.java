package com.sastix.csp.server.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sastix.csp.commons.model.*;
import com.sastix.csp.commons.model.elastic.ElasticData;
import com.sastix.csp.commons.model.elastic.ElasticDelete;
import com.sastix.csp.commons.model.elastic.ElasticSearchRequest;
import com.sastix.csp.commons.model.elastic.ElasticSearchResponse;
import com.sastix.csp.commons.model.elastic.query.Bool;
import com.sastix.csp.commons.model.elastic.query.Must;
import com.sastix.csp.commons.model.elastic.query.Query;
import com.sastix.csp.commons.model.elastic.query.Term;
import com.sastix.csp.commons.model.elastic.search.Hit;
import com.sastix.csp.server.service.CamelRestService;
import com.sastix.csp.server.service.CspUtils;
import org.apache.camel.*;
import org.apache.camel.component.http.HttpMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by iskitsas on 4/11/17.
 */
@Component
public class ElasticProcessor implements Processor {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticProcessor.class);

    @Value("${elastic.protocol}")
    String elasticProtocol;
    @Value("${elastic.host}")
    String elasticHost;
    @Value("${elastic.port}")
    String elasticPort;
    @Value("${elastic.path}")
    String elasticPath;


    @Produce
    ProducerTemplate producerTemplate;

    @Autowired
    CamelRestService camelRestService;

    @Autowired
    CspUtils cspUtils;

    @Override
    public void process(Exchange exchange) throws Exception {

        IntegrationData integrationData = cspUtils.getExchangeData(exchange, IntegrationData.class);
        String httpMethod = (String) exchange.getIn().getHeader(Exchange.HTTP_METHOD);
        IntegrationDataType dataType = integrationData.getDataType();


        /*
        DDL indexes data (DDL -> ELASTIC API)
         */
        if (httpMethod.equals(HttpMethods.POST.name())) {
            //create insert transaction object
            ElasticData elasticData = new ElasticData(integrationData.getDataParams(), integrationData.getDataObject());

            //query ES for insertion
            String response = camelRestService.send(this.getElasticURI() + "/" + dataType.toString().toLowerCase() + "?pretty", elasticData, httpMethod);
            LOG.info("ES Insert response: " + response);

        }
        else if (httpMethod.equals(HttpMethods.PUT.name())) {
            //create search transaction object
            ElasticSearchRequest elasticSearchRequest = this.getElasticSearchRequest(integrationData);

            //query ES to get IDs
            String response = camelRestService.send(this.getElasticURI() + "/" + dataType.toString().toLowerCase() + "/_search?pretty&_source=false", elasticSearchRequest, HttpMethods.POST.name());
            LOG.info("ES Search response: " + response);

            //create update transaction object
            ElasticData elasticData = new ElasticData(integrationData.getDataParams(), integrationData.getDataObject());

            ElasticSearchResponse elasticSearchResponse = new ObjectMapper().readValue(response, ElasticSearchResponse.class);
            for(Hit hit : elasticSearchResponse.getHits().getHits()) {
                LOG.info(hit.getId());
                //query ES to perform update
                String updateResponse = camelRestService.send(this.getElasticURI() + "/" + dataType.toString().toLowerCase() + "/" + hit.getId() + "", elasticData, HttpMethods.POST.name());
                LOG.info("ES Update index "+hit.getId()+" response: " + updateResponse);
            }

        }
        else if (httpMethod.equals(HttpMethods.DELETE.name())) {
            /**
             * Method 1. Camel does not transmits body in DELETE verbs
             */
            /*
            //create delete transaction object
            ElasticDelete elasticDelete = new ElasticDelete();
            elasticDelete.setQuery(this.getElasticQuery(integrationData));

            //query ES to perform deletion
            String response = camelRestService.send(this.getElasticURI() + "/" + dataType.toString().toLowerCase() + "/_query", elasticDelete, httpMethod);
            LOG.info("ES Delete response: " + response);
            */

            /**
             * Method 2
             */
            //create search transaction object
            ElasticSearchRequest elasticSearchRequest = this.getElasticSearchRequest(integrationData);

            //query ES to get IDs
            String response = camelRestService.send(this.getElasticURI() + "/" + dataType.toString().toLowerCase() + "/_search?pretty&_source=false", elasticSearchRequest, HttpMethods.POST.name());
            LOG.info("ES Search response: " + response);

            ElasticSearchResponse elasticSearchResponse = new ObjectMapper().readValue(response, ElasticSearchResponse.class);
            for(Hit hit : elasticSearchResponse.getHits().getHits()) {
                LOG.info(hit.getId());
                //query ES to perform deletion
                String deleteResponse = camelRestService.send(this.getElasticURI() + "/" + dataType.toString().toLowerCase() + "/" + hit.getId(), null, HttpMethods.DELETE.name());
                LOG.info("ES Delete index "+hit.getId()+"response: " + deleteResponse);
            }

        }


    }

    private String getElasticURI() {
        return elasticProtocol + "://" + elasticHost + ":" + elasticPort + elasticPath;
    }

    private Query getElasticQuery(IntegrationData integrationData) {

        Term t1 = new Term();
        t1.setRecordId(integrationData.getDataParams().getRecordId());

        Term t2 = new Term();
        t2.setCspId(integrationData.getDataParams().getCspId());

        Term t3 = new Term();
        t3.setApplicationId(integrationData.getDataParams().getApplicationId());

        Must m1 = new Must();
        m1.setTerm(t1);
        Must m2 = new Must();
        m2.setTerm(t2);
        Must m3 = new Must();
        m3.setTerm(t3);

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
