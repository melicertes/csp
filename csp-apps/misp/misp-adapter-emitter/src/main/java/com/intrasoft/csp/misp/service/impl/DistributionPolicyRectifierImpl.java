package com.intrasoft.csp.misp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.intrasoft.csp.misp.service.DistributionPolicyRectifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DistributionPolicyRectifierImpl implements DistributionPolicyRectifier {

    private static final Logger LOG = LoggerFactory.getLogger(DistributionPolicyRectifierImpl.class);

    @Override
    public JsonNode rectifyEvent(JsonNode jsonNode) {
        LOG.debug("Rectifying event...");
        // TODO Implementation

        int eventDistributionPolicyLevel = getEventDistributionPolicyLevel(jsonNode);

        JsonNode attributes = getEventAttributes(jsonNode);

        return jsonNode;
    }

    private int getEventDistributionPolicyLevel(JsonNode jsonNode) {
        JsonNode locatedNode = jsonNode.path("Event").path("distribution");
        int level = Integer.parseInt(locatedNode.textValue());
        return level;
    }

    private JsonNode getEventAttributes(JsonNode jsonNode) {
        return jsonNode.path("Event").path("Attribute");
    }

}
