package com.intrasoft.csp.misp.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface DistributionPolicyRectifier {

    JsonNode rectifyEvent(JsonNode jsonNode);

}
