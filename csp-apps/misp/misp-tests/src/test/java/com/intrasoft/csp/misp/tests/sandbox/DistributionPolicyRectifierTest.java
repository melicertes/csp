package com.intrasoft.csp.misp.tests.sandbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.intrasoft.csp.misp.service.DistributionPolicyRectifier;
import com.intrasoft.csp.misp.service.impl.DistributionPolicyRectifierImpl;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {DistributionPolicyRectifierImpl.class, ObjectMapper.class})
public class DistributionPolicyRectifierTest {

    final Logger LOG = LoggerFactory.getLogger(MispAdapterClientTest.class);

    @Autowired
    DistributionPolicyRectifier distributionPolicyRectifier;

    @Autowired
    ObjectMapper objectMapper;

    JsonNode jsonNode;

    Resource zmqEventSampleA = new ClassPathResource("json/zmqEventA.json");
    Resource zmqEventSamplePlain = new ClassPathResource("json/zmqEventPlain.json");

    @Before
    public void init() {
        jsonNode = getResourceAsJsonNode(zmqEventSampleA);
    }

    // The attribute with stricter sharing policy than the event should be deleted
    // The attribute with the same sharing policy with the event should not be deleted
    @Test
    public void rectifyEventShouldDeleteAttributeWithLowerDistributionLevelTest() {

        // Get all main the attributes of the event into an array and get hold of its size
        ArrayNode attributesArray = (ArrayNode) jsonNode.path("Event").path("Attribute");
        int initialNumberOfAttributes = attributesArray.size();

        distributionPolicyRectifier.rectifyEvent(jsonNode);

        // Array's size should be its initial size-1 after invoking the method in test
        attributesArray = (ArrayNode) jsonNode.path("Event").path("Attribute");
        assertTrue( attributesArray.size() == initialNumberOfAttributes-1);


        // Assert that attribute with id 11 is deleted (distribution=3) and attrib with id 1 is not (distrib=4)
        boolean idAfound = false;
        boolean idBfound = false;
        for (JsonNode attribute : attributesArray) {
            if (attribute.get("id").textValue().equals("11"))
              idAfound = true;
            if (attribute.get("id").textValue().equals("1"))
                idBfound = true;
        }
        assertFalse(idAfound);
        assertTrue(idBfound);

    }

    @Test
    public void rectifyEventSecondTest() {
        distributionPolicyRectifier.rectifyEvent(jsonNode);

    }


    private JsonNode getResourceAsJsonNode(Resource resource) {
        JsonNode jsonNode = null;
        String resourceString = getResourceAsString(resource);
        try {
            jsonNode = objectMapper.readTree(resourceString);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return jsonNode;
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
