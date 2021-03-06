package com.intrasoft.csp.anon.client.impl;

import com.intrasoft.csp.anon.client.AnonClient;
import com.intrasoft.csp.anon.commons.exceptions.InvalidDataTypeException;
import com.intrasoft.csp.anon.commons.model.AnonContextUrl;

import com.intrasoft.csp.anon.commons.model.IntegrationAnonData;

import com.intrasoft.csp.anon.commons.model.MappingDTO;
import com.intrasoft.csp.anon.commons.model.RuleSetDTO;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.libraries.versioning.client.ApiVersionClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Arrays;
import java.util.List;

/**
 * Created by chris on 14/7/2017.
 */
public class AnonClientImpl implements AnonClient, AnonContextUrl {
    private Logger LOG = (Logger) LoggerFactory.getLogger(AnonClientImpl.class);

    @Autowired
    @Qualifier("AnonApiVersionClient")
    ApiVersionClient apiVersionClient;


    @Autowired
    @Qualifier("AnonRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Override
    public IntegrationAnonData postAnonData(IntegrationAnonData integrationAnonData) throws InvalidDataTypeException {
        final String url = apiVersionClient.getApiUrl() + ANONYMIZE;
        LOG.info("ANON call [post]: " + url);
        IntegrationAnonData response = retryRestTemplate.postForObject(url, integrationAnonData, IntegrationAnonData.class);
        return response;
    }

    @Override
    public RuleSetDTO saveRuleSet(RuleSetDTO ruleSetDTO) {
        final String url = apiVersionClient.getApiUrl() + SAVE_RULESET;
        LOG.info("Save ruleSet call [post]: " + url);
        RuleSetDTO response = retryRestTemplate.postForObject(url, ruleSetDTO, RuleSetDTO.class);
        return response;
    }

    @Override
    public void deleteRuleSet(Long id) {
        final String url = apiVersionClient.getApiUrl() + DELETE_RULESET;
        LOG.info("Delete ruleSet call [post]: " + url);
        retryRestTemplate.postForObject(url, id, Long.class);
    }

    @Override
    public List<RuleSetDTO> getAllRuleSet() {
        final String url = apiVersionClient.getApiUrl() + GET_ALL_RULESET;
        LOG.info("Get all ruleSet call [get]: " + url);
        List<RuleSetDTO> response = Arrays.asList(retryRestTemplate.getForObject(url, RuleSetDTO[].class));
        return response;
    }

    @Override
    public RuleSetDTO getRuleSetById(Long id) {
        final String url = apiVersionClient.getApiUrl() + GET_RULESET+"/" + id;
        LOG.info("Get ruleSet call [get]: " + url);
        RuleSetDTO response = retryRestTemplate.getForObject(url, RuleSetDTO.class);
        return response;
    }

    @Override
    public MappingDTO saveMapping(MappingDTO mappingDTO) {
        final String url = apiVersionClient.getApiUrl() + SAVE_MAPPING;
        LOG.info("Save mapping call [post]: " + url);
        MappingDTO response = retryRestTemplate.postForObject(url, mappingDTO, MappingDTO.class);
        return response;
    }

    @Override
    public void deleteMapping(Long id) {
        final String url = apiVersionClient.getApiUrl() + DELETE_MAPPING;
        LOG.info("Delete mapping call [post]: " + url);
        retryRestTemplate.postForObject(url, id, Long.class);
    }

    @Override
    public MappingDTO getMappingById(Long id) {
        final String url = apiVersionClient.getApiUrl() + GET_MAPPING+"/{id}";
        LOG.info("Get ruleSet by id call [post]: " + url);
        MappingDTO response = retryRestTemplate.getForObject(url, MappingDTO.class,id);
        return response;
    }

    @Override
    public List<MappingDTO> getAllMappings() {
        final String url = apiVersionClient.getApiUrl() + GET_ALL_MAPPINGS;
        LOG.info("Get all ruleSet call [post]: " + url);
        List<MappingDTO> response = Arrays.asList(retryRestTemplate.getForObject(url, MappingDTO[].class));
        return response;
    }
}
