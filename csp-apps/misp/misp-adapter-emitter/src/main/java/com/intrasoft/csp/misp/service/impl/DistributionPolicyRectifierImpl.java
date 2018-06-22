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

        // TODO Implementation for Objects and their attributes

        deleteInvalidAttributes(jsonNode);


        return jsonNode;
    }

    private void deleteInvalidAttributes(JsonNode jsonNode) {
        // TODO switch for stand-alone attributes and object attributes
        ArrayNode attributesArray = (ArrayNode) jsonNode.path("Event").path("Attribute");
        int eventDistributionLevel = getEventDistributionPolicyLevel(jsonNode);
        List<String> idsToDelete = new ArrayList<>();

        attributesArray.forEach(attrib -> {
            int attributeDistributionLevel = attrib.path("distribution").asInt();
            if (attributeDistributionLevel < eventDistributionLevel) {
                idsToDelete.add(attrib.path("id").textValue());
            } else if (attributeDistributionLevel == 4 && attributeDistributionLevel == eventDistributionLevel) {
                if ( attrib.path("sharing_group_id").asInt() != jsonNode.path("Event").path("sharing_group_id").asInt()) {
                    idsToDelete.add(attrib.path("id").textValue());
                }
            }
        });

        int attribsDeleted = 0;
        while (attribsDeleted < idsToDelete.size()) {
            for (String id : idsToDelete) {
                for (int i = 0; i < attributesArray.size(); i++) {
                    if (attributesArray.get(i).path("id").textValue().equals(id)) {
                        attributesArray.remove(i);
                        attribsDeleted++;
                        break;
                    }
                }
            }
        }


    }

    private int getEventDistributionPolicyLevel(JsonNode jsonNode) {
        JsonNode locatedNode = jsonNode.path(MispEntity.EVENT.toString()).path("distribution");
        int level = Integer.parseInt(locatedNode.textValue());
        return level;
    }

    private JsonNode getEventAttributes(JsonNode jsonNode) {
        return jsonNode.path(MispEntity.EVENT.toString()).path("Attribute");
    }

}
