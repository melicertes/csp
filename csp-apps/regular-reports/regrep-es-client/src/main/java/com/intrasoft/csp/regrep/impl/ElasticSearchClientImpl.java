package com.intrasoft.csp.regrep.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.regrep.ElasticSearchClient;
import com.intrasoft.csp.regrep.commons.model.DailyExceptionsResponse;
import com.intrasoft.csp.regrep.commons.model.HitsItem;
import com.intrasoft.csp.regrep.routes.ContextUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ElasticSearchClientImpl implements ElasticSearchClient {

    @Autowired
    @Qualifier("ElasticRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Autowired
    ObjectMapper objectMapper;

    String context;
    String baseContextPath;
    HttpHeaders headers;

    private Logger LOG = LoggerFactory.getLogger(ElasticSearchClientImpl.class);

    public ElasticSearchClientImpl() {}

    public ElasticSearchClientImpl(String baseContextPath) {
        this.baseContextPath = baseContextPath;
    }

    @PostConstruct
    public void init(){
        context = baseContextPath;
    }

    @Override
    public void setProtocolHostPort(String protocol, String host, String port) {
        context = protocol+"://"+host+":"+port;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List acceptsList = new ArrayList();
        acceptsList.add(MediaType.APPLICATION_JSON);
        headers.setAccept(acceptsList);
    }

    @Override
    public String getContext() {
        return context;
    }

    @Override
    public int getNlogs(String requestBody) {
        String url = context + "/" + LOGS_INDEX + "/" + ContextUrl.Api.COUNT;
        int count = 0;
        try {
            count = getCount(requestBody, url);
        } catch (HttpClientErrorException e) {
            LOG.error(e.getMessage());
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return count;
    }

    @Override
    public int getNdocs(String requestBody) {
        String url = context + "/" + DATA_INDEX + "/" + ContextUrl.Api.COUNT;
        int count = 0;
        try {
            count = getCount(requestBody, url);
        } catch (HttpClientErrorException e) {
            LOG.error(e.getMessage());
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return count;
    }

    @Override
    public List<HitsItem> getLogData(String requestBody) {
        String url = context + "/" + LOGS_INDEX + "/" + ContextUrl.Api.SEARCH;  // prettify?
        LOG.info("API call [GET]: " + url);
        LOG.debug("API call [requestBody]: " + requestBody);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<DailyExceptionsResponse> responseEntity;
        try {
            responseEntity = retryRestTemplate.exchange(url, HttpMethod.GET, request, DailyExceptionsResponse.class);
            LOG.debug("Response: \n"+responseEntity.getBody().toString());
        } catch (Exception e) {
         LOG.error(e.getMessage());
         return null;
        }
        List<HitsItem> hitsItems = responseEntity.getBody().getHits().getHits();
        return hitsItems;
    }

    private int getCount(String requestBody, String url) {
        LOG.info("API call [GET]: " + url);
        LOG.debug("API call [requestBody]: " + requestBody);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = retryRestTemplate.exchange(url, HttpMethod.GET, request, String.class);
        LOG.debug("Response: \n"+response.getBody());
        JsonNode obj = null;
        try {
            obj = objectMapper.readTree(response.getBody());
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return Integer.parseInt(obj.get("count").toString());
    }

}
