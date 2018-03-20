package com.intrasoft.csp.client.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sastix.regrep.client.CspDataMappingType;
import com.sastix.regrep.client.ElasticSearchClient;
import com.sastix.regrep.client.routes.ContextUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ElasticSearchClientImpl implements ElasticSearchClient {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    String context;
    String baseContextPath;
    HttpHeaders headers;

    private Logger LOG = LoggerFactory.getLogger(ElasticSearchClientImpl.class);

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
    public int getNdocsByType(CspDataMappingType type, String requestBody) {
        String url = context + "/" + DATA_INDEX + "/" + type + "/" + ContextUrl.Api.COUNT;
        LOG.info("API call [GET]: " + url);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode obj = null;
        try {
            obj = mapper.readTree(response.getBody());
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return Integer.parseInt(obj.get("count").toString());
    }
}
