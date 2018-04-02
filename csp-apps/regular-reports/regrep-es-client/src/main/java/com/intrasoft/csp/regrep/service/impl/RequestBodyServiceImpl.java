package com.intrasoft.csp.regrep.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intrasoft.csp.regrep.CspDataMappingType;
import com.intrasoft.csp.regrep.DateMath;
import com.intrasoft.csp.regrep.LogstashMappingType;
import com.intrasoft.csp.regrep.service.RequestBodyService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

@Service
public class RequestBodyServiceImpl implements RequestBodyService {

    private static Logger LOG = LoggerFactory.getLogger(RequestBodyServiceImpl.class);

    @Autowired
    ObjectMapper objectMapper;

    URL nLogsByType = getClass().getClassLoader().getResource("json.payloads/nlogs-by-type.json");
    URL nDocsByType = getClass().getClassLoader().getResource("json.payloads/ndocs-by-type.json");

    @Override
    public String buildRequestBody(DateMath gte, DateMath lt, LogstashMappingType type) {

        String payload = null;
        try {
            payload = FileUtils.readFileToString(new File(nLogsByType.toURI()), Charset.forName("UTF-8"));
        } catch (IOException e) {
            LOG.error(e.getMessage());
        } catch (URISyntaxException e) {
            LOG.error(e.getMessage());
        }

        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(payload);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

        ( (ObjectNode) jsonNode).findParent("gte").put("gte", "now-" + gte.toString());
        ( (ObjectNode) jsonNode).findParent("lt").put("lt", lt.toString());
        if (type.equals(LogstashMappingType.ALL))
            ((ObjectNode) jsonNode).findParent("match").remove("match");
        else
            ( (ObjectNode) jsonNode).findParent("logtype").put("logtype", type.toString());
        payload = jsonNode.toString();
        return payload;
    }

    @Override
    public String buildRequestBody(DateMath gte, DateMath lt, CspDataMappingType type) {
        String payload = null;
        try {
            payload = FileUtils.readFileToString(new File(nDocsByType.toURI()), Charset.forName("UTF-8"));
        } catch (IOException e) {
            LOG.error(e.getMessage());
        } catch (URISyntaxException e) {
            LOG.error(e.getMessage());
        }

        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(payload);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

        ( (ObjectNode) jsonNode).findParent("gte").put("gte", "now-" + gte.toString());
        ( (ObjectNode) jsonNode).findParent("lt").put("lt", lt.toString());
        if (type.equals(CspDataMappingType.ALL))
            ((ObjectNode) jsonNode).findParent("match").remove("match");
        else
            ( (ObjectNode) jsonNode).findParent("_type").put("_type", type.toString());
        payload = jsonNode.toString();
        return payload;
    }


}
