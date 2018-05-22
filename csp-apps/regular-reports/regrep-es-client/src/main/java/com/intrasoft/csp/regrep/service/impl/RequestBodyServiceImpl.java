package com.intrasoft.csp.regrep.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intrasoft.csp.regrep.CspDataMappingType;
import com.intrasoft.csp.regrep.DateMath;
import com.intrasoft.csp.regrep.LogstashMappingType;
import com.intrasoft.csp.regrep.service.RequestBodyService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RequestBodyServiceImpl implements RequestBodyService {

    private static Logger LOG = LoggerFactory.getLogger(RequestBodyServiceImpl.class);

    @Autowired
    ObjectMapper objectMapper;

    String payload;

    Resource nLogsByType = new ClassPathResource("json.payloads/nlogs-by-type.json");
    Resource nDocsByType = new ClassPathResource("json.payloads/ndocs-by-type.json");
    Resource dailyExcLogs= new ClassPathResource("json.payloads/daily-exc-logs.json");

    final String TIME_DIF = "-9h";

    @Override
    public String buildRequestBody(DateMath gte, DateMath lt, LogstashMappingType type) {

        payload = getResourceAsString(nLogsByType);

        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(payload);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

        ( (ObjectNode) jsonNode).findParent("gte").put("gte", "now-" + gte.toString() + TIME_DIF);
        ( (ObjectNode) jsonNode).findParent("lt").put("lt", lt.toString() + TIME_DIF);
        if (type.equals(LogstashMappingType.ALL))
            ((ObjectNode) jsonNode).findParent("match").remove("match");
        else
            ( (ObjectNode) jsonNode).findParent("logtype").put("logtype", type.toString());
        payload = jsonNode.toString();
        return payload;
    }

    @Override
    public String buildRequestBody(DateMath gte, DateMath lt, CspDataMappingType type) {

        payload = getResourceAsString(nDocsByType);

        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(payload);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

        ( (ObjectNode) jsonNode).findParent("gte").put("gte", "now-" + gte.toString() + TIME_DIF);
        ( (ObjectNode) jsonNode).findParent("lt").put("lt", lt.toString() + TIME_DIF);
        if (type.equals(CspDataMappingType.ALL))
            ((ObjectNode) jsonNode).findParent("match").remove("match");
        else
            ( (ObjectNode) jsonNode).findParent("_type").put("_type", type.toString());
        payload = jsonNode.toString();
        return payload;
    }

    @Override
    public String buildRequestBodyForLogs(DateMath gte, DateMath lt, LogstashMappingType type) {

        payload = getResourceAsString(dailyExcLogs);

        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(payload);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

        ( (ObjectNode) jsonNode).findParent("gte").put("gte", "now-" + gte.toString() + TIME_DIF);
        ( (ObjectNode) jsonNode).findParent("lt").put("lt", lt.toString() + TIME_DIF);

        if (type.equals(LogstashMappingType.ALL))
            ((ObjectNode) jsonNode).findParent("match").remove("match");
        else
            ( (ObjectNode) jsonNode).findParent("logtype").put("logtype", type.toString());
        payload = jsonNode.toString();
        return payload;
    }

    private String getResourceAsString(Resource resource) {

        try {
            return IOUtils.toString(resource.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
