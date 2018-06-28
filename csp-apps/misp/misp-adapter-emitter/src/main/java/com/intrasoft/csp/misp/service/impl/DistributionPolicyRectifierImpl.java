package com.intrasoft.csp.misp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.intrasoft.csp.misp.commons.config.MispContextUrl;
import com.intrasoft.csp.misp.service.DistributionPolicy;
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

        int eventDistributionLevel = getEventDistributionPolicyLevel(jsonNode);
        int eventSharinggroupId = jsonNode.path("Event").path("sharing_group_id").asInt();

        MispEntity[] entitiesArray = {MispEntity.ATTRIBUTE, MispEntity.OBJECT};

        for (MispEntity entity : entitiesArray) {
            ArrayNode entityArray = (ArrayNode) jsonNode.path(MispEntity.EVENT.toString()).path(entity.toString());
            // LOG.debug(entity.toString() + " occurances: " + entityArray.size());
            if (entityArray.size()>0) {
                if (entity.equals(MispEntity.OBJECT)) {
                    // First pass
                    removeNonShareableNodes(entityArray, eventDistributionLevel, eventSharinggroupId);
                    // Object attributes
                    ArrayNode objAttribsArray;
                    for (JsonNode eventObject : entityArray) {
                        objAttribsArray = (ArrayNode) eventObject.path(MispEntity.ATTRIBUTE.toString());
                        removeNonShareableNodes(objAttribsArray, eventDistributionLevel, eventSharinggroupId);
                    }
                }
                removeNonShareableNodes(entityArray, eventDistributionLevel, eventSharinggroupId);
            }
        }

        return jsonNode;
    }

    private void removeNonShareableNodes(ArrayNode arrayNode, int eventDistributionLevel, int eventSharingGroupId) {

        List<String> idsToDelete = new ArrayList<>();

        arrayNode.forEach(node -> {
            int attributeDistributionLevel = node.path("distribution").asInt();
            if (attributeDistributionLevel < eventDistributionLevel) {
                idsToDelete.add(node.path("id").textValue());
            } else if (attributeDistributionLevel == DistributionPolicy.SHARING_GROUP.getLevel() && attributeDistributionLevel == eventDistributionLevel) {
                if ( node.path("sharing_group_id").asInt() != eventSharingGroupId) {
                    idsToDelete.add(node.path("id").textValue());
                }
            }
        });

        int attribsDeleted = 0;
        while (attribsDeleted < idsToDelete.size()) {
            for (String id : idsToDelete) {
                for (int i = 0; i < arrayNode.size(); i++) {
                    if (arrayNode.get(i).path("id").textValue().equals(id)) {
                        arrayNode.remove(i);
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
        return jsonNode.path(MispEntity.EVENT.toString()).path(MispEntity.ATTRIBUTE.toString());
    }

}
