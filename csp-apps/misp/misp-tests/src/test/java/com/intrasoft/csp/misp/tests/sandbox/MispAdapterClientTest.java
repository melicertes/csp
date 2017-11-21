package com.intrasoft.csp.misp.tests.sandbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intrasoft.csp.commons.model.DataParams;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.SharingParams;
import com.intrasoft.csp.misp.MispAdapterEmitterApplication;
import com.intrasoft.csp.misp.client.MispClient;
import com.intrasoft.csp.misp.client.config.MispClientConfig;
import com.intrasoft.csp.misp.service.impl.AdapterDataHandlerImpl;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {MispAdapterEmitterApplication.class, MispClient.class, MispClientConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
        "misp.app.protocol:http",
                "misp.app.host:misp.dimitris.dk",
                "misp.app.port:80",
                "misp.app.authorization.key:JNqWBxfPiIywz7hUe58MyJf6sD5PrTVaGm7hTn6c",
                "zeromq.protocol:tcp",
                "zeromq.host:localhost",
                "zeromq.port:50000",
                "zeromq.topic:misp_json",
                "server.name:LOCAL-CERT",
                //"logging.level.:trace",
                "adapter.server.protocol:http",
                "adapter.server.host:localhost",
                "adapter.server.port:8081",
                "elastic.protocol:http",
                "elastic.host:csp0.dangerduck.gr",
                "elastic.port:9200",
                "elastic.path:",
                "retry.backOffPeriod:5000",
                "app.authorization.key:JNqWBxfPiIywz7hUe58MyJf6sD5PrTVaGm7hTn6c",
                "apache.camel.use.activemq:false",
                "retry.backOffPeriod:5000",
                "retry.maxAttempts:3",
                "client.ssl.enabled:false",
                "consume.errorq.max.messages:3"})
public class MispAdapterClientTest {

    final Logger LOG = LoggerFactory.getLogger(MispAdapterClientTest.class);

    @Autowired
    @Qualifier("MispClient")
    MispClient mispClient;

    @Test
    public void testPost() throws JsonProcessingException {
        DataParams dataParams = new DataParams();
        dataParams.setCspId("LOCAL-CERT");
        dataParams.setApplicationId("misp");
        dataParams.setRecordId("222");
        dataParams.setDateTime(new DateTime());
        dataParams.setOriginCspId("LOCAL-CERT");
        dataParams.setOriginApplicationId("misp");
        dataParams.setOriginRecordId("222");
        dataParams.setUrl("url");

        SharingParams sharingParams = new SharingParams();
        sharingParams.setIsExternal(false);
        sharingParams.setToShare(true);
        sharingParams.setTcId(null);
        sharingParams.setTeamId(null);

        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataParams(dataParams);
        integrationData.setSharingParams(sharingParams);
        integrationData.setDataObject(loadJsonFromFile());

        integrationData.setDataType(IntegrationDataType.EVENT);
        try {
            LOG.info(new ObjectMapper().writeValueAsString(integrationData));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        mispClient.postIntegrationDataAdapter(integrationData);
    }

    public JsonNode loadJsonFromFile() throws JsonProcessingException {
        URL url = getClass().getClassLoader().getResource("json/event.json");
        File file = new File(url.getFile());
        String prettyEvent = null;
        String event = null;
        JsonNode jsonNode = null;
        try {
            prettyEvent = new String(Files.readAllBytes(Paths.get(url.toURI())));
            ObjectMapper objectMapper = new ObjectMapper();
            jsonNode = objectMapper.readValue(prettyEvent, JsonNode.class);
            event = jsonNode.toString();
            LOG.info("EVENT: " + prettyEvent);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return jsonNode;
    }
}
