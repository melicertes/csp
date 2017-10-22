package com.intrasoft.csp.misp.commons.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class JsonObjectHandler {

    public static String readField(String json, String name) throws IOException {
        JsonNode object = new ObjectMapper().readTree(json);
        JsonNode node = object.path("Event").get(name);
        return (node == null ? null : node.textValue());
    }

    public static String updateField(String json, String name, String value) throws IOException {
        JsonNode object = new ObjectMapper().readTree(json);
        JsonNode node = object.path("Event");
        ObjectNode objectNode = (ObjectNode) node;
        objectNode.put(name, value);
        return object.toString();
    }

}
