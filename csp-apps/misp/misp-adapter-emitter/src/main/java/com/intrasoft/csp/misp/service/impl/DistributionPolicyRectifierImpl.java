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
        return jsonNode;
    }
}
