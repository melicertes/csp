package com.intrasoft.csp.misp.tests.sandbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Before
    public void init() {
        jsonNode = getResourceAsJsonNode(zmqEventSampleA);
    }

    // Attribute with stricter sharing policy than the event should be deleted
    @Test
    public void rectifyEventFirstTest() {
        distributionPolicyRectifier.rectifyEvent(jsonNode);
        // TODO test
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
