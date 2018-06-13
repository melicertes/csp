package com.intrasoft.csp.misp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.intrasoft.csp.misp.commons.config.MispContextUrl;
import com.intrasoft.csp.misp.service.DistributionPolicyRectifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DistributionPolicyRectifierImpl implements DistributionPolicyRectifier, MispContextUrl {

    private static final Logger LOG = LoggerFactory.getLogger(DistributionPolicyRectifierImpl.class);

    @Override
    public JsonNode rectifyEvent(JsonNode jsonNode) {
        LOG.debug("Rectifying event...");
        // TODO Implementation

        int eventDistributionLevel = getEventDistributionPolicyLevel(jsonNode);

        JsonNode attributes = getEventAttributes(jsonNode);
        ArrayNode attributesArray = (ArrayNode) jsonNode.path("Event").path("Attribute");
        List<Integer> indexesToDelete = new ArrayList<>();

        // Iterate any attributes and remove matches with lower distribution level from the event
        for (int i = 0; i<attributesArray.size(); i++) {
            int attributeDistributionLevel = Integer.parseInt(attributesArray.get(i).path("distribution").textValue());
            if (attributeDistributionLevel < eventDistributionLevel) {
                indexesToDelete.add(i);
            }
        }

        indexesToDelete.forEach(attributesArray::remove);

        return jsonNode;
    }

    private int getEventDistributionPolicyLevel(JsonNode jsonNode) {
        JsonNode locatedNode = jsonNode.path(MispEntity.EVENT.toString()).path("distribution");
        int level = Integer.parseInt(locatedNode.textValue());
        return level;
    }

    private JsonNode getEventAttributes(JsonNode jsonNode) {
        return jsonNode.path("Event").path("Attribute");
    }

}
