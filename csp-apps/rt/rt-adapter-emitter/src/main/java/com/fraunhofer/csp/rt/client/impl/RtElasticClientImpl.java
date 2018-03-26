package com.fraunhofer.csp.rt.client.impl;

import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraunhofer.csp.rt.client.RtElasticClient;
import com.intrasoft.csp.client.impl.ElasticClientImpl;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.elastic.ElasticSearchRequest;
import com.intrasoft.csp.commons.model.elastic.ElasticSearchResponse;
import com.intrasoft.csp.commons.model.elastic.query.Bool;
import com.intrasoft.csp.commons.model.elastic.query.Filter;
import com.intrasoft.csp.commons.model.elastic.query.Query;
import com.intrasoft.csp.commons.model.elastic.query.Term;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;

/**
 * Created by Majid Salehi on 4/8/17.
 */
public class RtElasticClientImpl extends ElasticClientImpl implements RtElasticClient {

	private static final Logger LOG = LoggerFactory.getLogger(RtElasticClientImpl.class);

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

	@Override
	public boolean objectExists(String uuid, IntegrationDataType datatype) throws IOException {
		ElasticSearchRequest elasticSearchRequest = this.getElasticSearchRequestForUuid(uuid);
		LOG.debug(this.getElasticURI() + datatype.toString().toLowerCase() + "/_search?pretty&_source=false");
		LOG.debug(elasticSearchRequest.toString());
		ResponseEntity<String> response = retryRestTemplate.postForEntity(
				this.getElasticURI() + "/" + datatype.toString().toLowerCase() + "/_search?pretty&_source=false",
				elasticSearchRequest, String.class);
		// LOG.debug("Elastic - ES Search response: " + response);

		if (response == null) {
			// TODO: What do we want here
			LOG.debug("Response from ES is null");
		}

		ElasticSearchResponse elasticSearchResponse = new ObjectMapper().readValue(response.getBody(),
				ElasticSearchResponse.class);
		if (elasticSearchResponse.getHits().getTotal() == 0)
			return false;
		else
			return true;
	}

	private String getElasticURI() {
		return elasticProtocol + "://" + elasticHost + ":" + elasticPort + "/" + elasticPath;
	}

	private ElasticSearchRequest getElasticSearchRequestForUuid(String uuid) {

		ElasticSearchRequest elasticSearchRequest = new ElasticSearchRequest();
		elasticSearchRequest.setQuery(this.getElasticQueryForUuid(uuid));

		return elasticSearchRequest;
	}

	private Query getElasticQueryForUuid(String uuid) {
		LOG.debug("getElasticQueryForUuid:" + uuid);

		Term t1 = new Term();
		t1.setRecordId(uuid);

		Filter m1 = new Filter();
		m1.setTerm(t1);

		ArrayList<Filter> filter = new ArrayList<>();
		filter.add(m1);

		Bool bool = new Bool();
		bool.setFilter(filter);

		Query query = new Query();
		query.setBool(bool);

		return query;
	}
}
