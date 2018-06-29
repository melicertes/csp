package com.intrasoft.csp.misp.tests.sandbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

import static com.intrasoft.csp.misp.commons.config.MispContextUrl.*;
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

    JsonNode event;

    Resource zmqEventSampleA = new ClassPathResource("json/zmqEventA.json");
    Resource zmqEventWithAttributes = new ClassPathResource("json/zmqEventWithAttribs.json");
    Resource zmqEventWithObjects = new ClassPathResource("json/zmqEventWithObjs.json");

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

        // Search in attributes array for ids that should not be present (deleted by the method in test)
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

        // Search in attributes array for ids that should not be present (deleted by the method in test)
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
    //    - Objects having "Inherit event" as their distribution policy must not be deleted (object ids 3,4)
    //    - Objects having a stricter policy must be deleted (object ids 5,6)
    // ** This is an object-level test (not testing nested attributes)
    @Test
    public void rectifyEventShouldKeepTwoObjectsTest() {

        event = getResourceAsJsonNode(zmqEventWithObjects);

        // Get the event's objects into an array and get hold of its size
        ArrayNode objectsArray = (ArrayNode) event.path(MispEntity.EVENT.toString()).path(MispEntity.OBJECT.toString());
        int initialNumberOfObjects = objectsArray.size();

        // Objects' ids
        int[] shouldKeepObjIds = {3, 4};
        int[] shouldDeleteObjIds = {5, 6};

        Map<Integer, Boolean> objectsMap = new HashMap<>();
        for (int id : shouldDeleteObjIds) {
            objectsMap.put(id, false);
        }

        List<Integer> objIdsFound = new ArrayList<>();

        distributionPolicyRectifier.rectifyEvent(event);

        // Search in attributes array for ids that should be present
        for (int objToKeepId : shouldKeepObjIds) {
            objectsArray.forEach(attrib -> {
                if (attrib.get("id").textValue().equals(String.valueOf(objToKeepId)))
                    objIdsFound.add(objToKeepId);
            });
        }
        assertTrue( (objIdsFound.contains(shouldKeepObjIds[0])));
        assertTrue( (objIdsFound.contains(shouldKeepObjIds[1])));

        // Search in attributes array for ids that should not be present (deleted by the method in test)
        for (int id : shouldDeleteObjIds) {
            objectsArray.forEach(attrib -> {
                if (attrib.get("id").asInt() == id)
                    objectsMap.replace(id, true);
            });
        }

        objectsMap.forEach( (k,v) -> {
            assertFalse(v);
        });

    }

/*
     According to SXCSP-505, for the given resource:
       - Objects having "Inherit event" as their distribution policy must not be deleted, as well as their
         attributes as long as they follow the rules described in SXCSP-505 (in this case, all of the object's
         attributes also have a distribution policy of "Inherit event").
         Therefore, Object with Id 3 and all of its attributes should not be deleted.
       - Objects having the same distribution policy as the event must not be deleted. However, each of their
         attributes have their own distribution policy, hence they should follow the rules described in SXCSP-505.
         Object with Id 4 should not be deleted, but any of its attributes not following the distribution policy
         rules should. Therefore:
           - Attribute with Id 27 should be deleted because although it's distribution policy is the same as
             the event's (sharing group), it has a different sharing group id, which is not allowed and should be deleted.
           - Attributes with Ids 28 and 32 should be deleted (have a stricter distribution policy than the event).
           - Attributes with Ids 29,30,31, 33, 34 should not be deleted (inherit event policy).
*/
    @Test
    public void rectifyEventObjectAttributesTest() {

        event = getResourceAsJsonNode(zmqEventWithObjects);

        // Get the event's objects
        ArrayNode objectsArray = (ArrayNode) event.path(MispEntity.EVENT.toString()).path(MispEntity.OBJECT.toString());

        int objId3expectedNumberOfAttributes = 9;
        int objId4expectedNumberOfAttributes = 5;

        // Expected attribute Ids
        int[] shouldKeepIds = { 29, 30, 31, 33, 34 };
        int[] shouldDeleteIds = { 27, 28, 32 };

        Map<Integer, Boolean> attribMap = new HashMap<>();
        for (int id : shouldDeleteIds) {
            attribMap.put(id, false);
        }

        List<Integer> idsFound = new ArrayList<>();

        // Invoke the method in test
        distributionPolicyRectifier.rectifyEvent(event);

        // Assert that we have the expected number of attributes for each object
        for (JsonNode object : objectsArray) {
            if (object.path("id").asInt()==3) {
                ArrayNode objId3AttribsArray = (ArrayNode) object.path(MispEntity.ATTRIBUTE.toString());
                assertTrue(objId3AttribsArray.size()==objId3expectedNumberOfAttributes);
            }
            if (object.path("id").asInt()==4) {
                ArrayNode objId4AttribsArray = (ArrayNode) object.path(MispEntity.ATTRIBUTE.toString());
                assertTrue(objId4AttribsArray.size()==objId4expectedNumberOfAttributes);
                for (int attribToKeepId : shouldKeepIds) {
                    objId4AttribsArray.forEach(attrib -> {
                        if (attrib.get("id").textValue().equals(String.valueOf(attribToKeepId)))
                            idsFound.add(attribToKeepId);
                    });
                }
                for (int id : shouldDeleteIds) {
                    objId4AttribsArray.forEach(attrib -> {
                        if (attrib.get("id").asInt() == id)
                            attribMap.replace(id, true);
                    });
                }
            }
        }


        for (int id : shouldKeepIds) {
            assertTrue( (idsFound.contains(id)));
        }

        attribMap.forEach( (k,v) -> {
            assertFalse(v);
        });


    }

    // According to SXCSP-505, distribution level should be changed:
    //   - this community        -> your organisation (1 -> 0)
    //   - connected communities -> this community    (2 -> 1)
    @Test
    public void rectifyEventChangesEventDistributionLevelTest() {

        event = getResourceAsJsonNode(zmqEventWithObjects);

        // Modifying resource's distribution level in-memory for this test.
        // Scenario A: Event's distribution level is 1 ("This community").
        ( (ObjectNode) event).findParent("distribution").put("distribution", "1");

        distributionPolicyRectifier.rectifyEvent(event);

        assertTrue(event.path(MispEntity.EVENT.toString()).path("distribution").asInt()==0);

        // Scenario B: Event's distribution level is 2 ("Connected communities").
        ( (ObjectNode) event).findParent("distribution").put("distribution", "2");

        distributionPolicyRectifier.rectifyEvent(event);

        assertTrue(event.path(MispEntity.EVENT.toString()).path("distribution").asInt()==1);

        // Scenario C: Event's distribution level is > 2 and should stay the same
        ( (ObjectNode) event).findParent("distribution").put("distribution", "3");

        distributionPolicyRectifier.rectifyEvent(event);

        assertTrue(event.path(MispEntity.EVENT.toString()).path("distribution").asInt()==3);

        // Scenario D: Event's distribution level is < 1 and should stay the same
        ( (ObjectNode) event).findParent("distribution").put("distribution", "0");

        distributionPolicyRectifier.rectifyEvent(event);

        assertTrue(event.path(MispEntity.EVENT.toString()).path("distribution").asInt()==0);

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
