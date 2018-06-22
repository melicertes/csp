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
import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {DistributionPolicyRectifierImpl.class, ObjectMapper.class})
public class DistributionPolicyRectifierTest {

    final Logger LOG = LoggerFactory.getLogger(MispAdapterClientTest.class);

    @Autowired
    DistributionPolicyRectifier distributionPolicyRectifier;

    @Autowired
    ObjectMapper objectMapper;

    JsonNode event;

    Resource zmqEventSampleA = new ClassPathResource("json/zmqEventA.json");
    Resource zmqEventWithAttributes = new ClassPathResource("json/zmqEventWithAttribs.json");
    Resource zmqEventSamplePlain = new ClassPathResource("json/zmqEventPlain.json");

    @Before
    public void init() {
    }

    // According to SXCSP-505, for the given resource:
    //   - The attribute having the same distribution policy as the event, but a different Sharing Group id
    //     than the event, must be deleted (attrib with id 1).
    //   - The attribute having a wider distribution policy than the event must not be deleted (attrib with id 10)
    //   - The attribute having a stricter distribution policy than the event must be deleted (attrib with id 11)
    @Test
    public void rectifyEventShouldDeleteCertainAttributesTest() {

        event = getResourceAsJsonNode(zmqEventSampleA);

        // Get all main the attributes of the event into an array and get hold of its size
        ArrayNode attributesArray = (ArrayNode) event.path("Event").path("Attribute");
        int initialNumberOfAttributes = attributesArray.size();

        // Attributes ids
        int[] shouldKeepIds = { 10 };
        int[] shouldDeleteIds = { 1, 11 };

        Map<Integer, Boolean> attribMap = new HashMap<>();
        for (int id : shouldDeleteIds) {
            attribMap.put(id, false);
        }

        List<Integer> idsFound = new ArrayList<>();


        distributionPolicyRectifier.rectifyEvent(event);

        // Array's size should be its initial size - 2 after invoking the method in test
        assertTrue( attributesArray.size() == initialNumberOfAttributes-2);

        // Search in attributes array for ids that should be present
        for (int attribToKeepId : shouldKeepIds) {
            attributesArray.forEach(attrib -> {
                if (attrib.get("id").textValue().equals(String.valueOf(attribToKeepId)))
                    idsFound.add(attribToKeepId);
            });
        }

        assertTrue( (idsFound.contains(shouldKeepIds[0])));

        // Search in attributes array for ids that should not be present (deleted by method in test)
        for (int id : shouldDeleteIds) {
            attributesArray.forEach(attrib -> {
                if (attrib.get("id").asInt() == id)
                    attribMap.replace(id, true);
            });
        }

        attribMap.forEach( (k,v) -> {
            assertFalse(v);
        });


    }

    // According to SXCSP-505, for the given resource:
    //   - Attributes having the same sharing group id as the event should not be deleted (attrib with id 14)
    //   - Attributes having "Inherit event" as their distribution policy should not be deleted (attrib with id 15)
    //   - Attributes having a different sharing group id than the event should be deleted (attrib with id 16)
    //   - Attributes having a lower distribution setting than the event should be deleted (attrib with id 17)
    @Test
    public void rectifyEventShouldKeepCertainAttributesTest() {

        event = getResourceAsJsonNode(zmqEventWithAttributes);

        // Get all main the attributes of the event into an array and get hold of its size
        ArrayNode attributesArray = (ArrayNode) event.path("Event").path("Attribute");
        int initialNumberOfAttributes = attributesArray.size();

        // Attributes ids
        int[] shouldKeepIds = {14, 15};
        int[] shouldDeleteIds = {16, 17};

        Map<Integer, Boolean> attribMap = new HashMap<>();
        for (int id : shouldDeleteIds) {
            attribMap.put(id, false);
        }

        List<Integer> idsFound = new ArrayList<>();

        distributionPolicyRectifier.rectifyEvent(event);

        // Search in attributes array for ids that should be present
        for (int attribToKeepId : shouldKeepIds) {
            attributesArray.forEach(attrib -> {
                if (attrib.get("id").textValue().equals(String.valueOf(attribToKeepId)))
                    idsFound.add(attribToKeepId);
            });
        }
        assertTrue( (idsFound.contains(shouldKeepIds[0])));
        assertTrue( (idsFound.contains(shouldKeepIds[1])));

        // Search in attributes array for ids that should not be present (deleted by method in test)
        for (int id : shouldDeleteIds) {
            attributesArray.forEach(attrib -> {
                if (attrib.get("id").asInt() == id)
                    attribMap.replace(id, true);
            });
        }

        attribMap.forEach( (k,v) -> {
            assertFalse(v);
        });

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
