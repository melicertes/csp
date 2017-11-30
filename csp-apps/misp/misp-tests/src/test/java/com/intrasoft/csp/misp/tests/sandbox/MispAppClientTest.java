package com.intrasoft.csp.misp.tests.sandbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intrasoft.csp.misp.client.MispAppClient;
import com.intrasoft.csp.misp.client.config.MispAppClientConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.intrasoft.csp.misp.commons.utils.JsonObjectHandler.readField;
import static com.intrasoft.csp.misp.commons.utils.JsonObjectHandler.updateField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {MispAppClient.class, MispAppClientConfig.class},
        properties = {
                "misp.app.protocol:http",
                "misp.app.host:192.168.56.50",
                "misp.app.port:80",
                "misp.app.authorization.key:JNqWBxfPiIywz7hUe58MyJf6sD5PrTVaGm7hTn6c"})
// misp.app.host:misp.dimitris.dk
public class MispAppClientTest {
    private static final Logger LOG = LoggerFactory.getLogger(MispAppClientTest.class);

    @Value("${misp.app.protocol}")
    String protocol;

    @Value("${misp.app.host}")
    String host;

    @Value("${misp.app.port}")
    String port;

    @Value("${misp.app.authorization.key}")
    String authorizationKey;

    @Autowired
    @Qualifier(value = "MispAppClient")
    MispAppClient mispAppClient;

    @Test
    public void addMispEventTest() throws URISyntaxException, IOException {
        ResponseEntity<String> response = postEvent();
        LOG.info(response.getBody().toString());
        assertThat(response.getStatusCodeValue(), is(200));
    }

    @Test
    public void getMispEventTest() throws URISyntaxException, IOException {
        ResponseEntity<String> responseEntity = mispAppClient.getMispEvent("5a12bb50-fcb4-4345-9bae-619a9e459fec");
        LOG.info(responseEntity.getBody().toString());
        assertThat(responseEntity.getStatusCodeValue(), is(200));
    }

    @Test
    public void updateMispEventByUUIDTest() throws URISyntaxException, IOException {

        ResponseEntity<String> postResponse = postEvent();
        String postResponseBody = postResponse.getBody();
        String uuid = readField( postResponseBody, "uuid");

        mispAppClient.setProtocolHostPortHeaders(protocol, host, port, authorizationKey);
        String putRequestBody = updateField(postResponseBody, "info", "bbbbb");
        assertThat((new ObjectMapper().readTree(postResponseBody).path("Event")).get("info").toString(), containsString("aaaaaa"));
        ResponseEntity<String> response = mispAppClient.updateMispEvent(uuid, putRequestBody);
        LOG.info(response.toString());
        assertThat(response.getStatusCodeValue(), is(200));
        assertThat((new ObjectMapper().readTree(response.getBody()).path("Event")).get("info").toString(), containsString("bbbbb"));
    }

    @Test
    public void updateMispEventByIDTest() throws URISyntaxException, IOException {

        ResponseEntity<String> postResponse = postEvent();
        LOG.info(postResponse.getBody().toString());

        mispAppClient.setProtocolHostPortHeaders(protocol, host, port, authorizationKey);
//        String putRequestBody = updateField(postResponseBody, "info", "bbbbb");
//        assertThat((new ObjectMapper().readTree(postResponseBody).path("Event")).get("info").toString(), containsString("aaaaaa"));
        ResponseEntity<String> response = mispAppClient.updateMispEvent(postResponse.getBody());
        LOG.info(response.toString());
        assertThat(response.getStatusCodeValue(), is(200));
        assertThat((new ObjectMapper().readTree(response.getBody()).path("Event")).get("info").toString(), containsString("bbbbb"));
    }

    @Test
    public void updateMispTest() throws URISyntaxException, IOException {

        ResponseEntity<String> postResponse = postEvent();
        String postResponseBody = postResponse.getBody();
        String id = readField( postResponseBody, "id");
        LOG.info(postResponseBody);

        mispAppClient.setProtocolHostPortHeaders(protocol, host, port, authorizationKey);
//        String putRequestBody = updateField(postResponseBody, "info", "bbbbb");
//        assertThat((new ObjectMapper().readTree(postResponseBody).path("Event")).get("info").toString(), containsString("aaaaaa"));
        ResponseEntity<String> response = mispAppClient.updateMispEvent(postResponseBody);
        LOG.info(response.toString());
//        assertThat(response.getStatusCodeValue(), is(200));
//        assertThat((new ObjectMapper().readTree(response.getBody()).path("Event")).get("info").toString(), containsString("bbbbb"));
    }

    @Test
    public void deleteMispEventByUUIDTest() throws URISyntaxException, IOException {

        ResponseEntity<String> postResponse = postEvent();

        String id = readField( postResponse.getBody(), "id");
        String uuid = readField( postResponse.getBody(), "uuid");
        LOG.info(uuid);
        LOG.info(id);
        mispAppClient.setProtocolHostPortHeaders(protocol, host, port, authorizationKey);
        ResponseEntity<String> response = mispAppClient.deleteMispEvent(uuid);
        LOG.info(response.toString());
        assertThat(response.getStatusCodeValue(), is(200));
        assertThat(response.getBody(), containsString("Event deleted"));
    }

    @Test
    public void deleteMispEventByIDTest() throws URISyntaxException, IOException {

        ResponseEntity<String> postResponse = postEvent();

        String id = readField( postResponse.getBody(), "id");
        String uuid = readField( postResponse.getBody(), "uuid");
        LOG.info(uuid);
        LOG.info(id);
        mispAppClient.setProtocolHostPortHeaders(protocol, host, port, authorizationKey);
        ResponseEntity<String> response = mispAppClient.deleteMispEvent(id);
        LOG.info(response.toString());
        assertThat(response.getStatusCodeValue(), is(200));
        assertThat(response.getBody(), containsString("Event deleted"));
    }

    private ResponseEntity<String> postEvent () throws URISyntaxException, IOException {
        URL url = getClass().getClassLoader().getResource("json/event.json");
        File file = new File(url.getFile());
        String event = new String(Files.readAllBytes(Paths.get(url.toURI())));
        mispAppClient.setProtocolHostPortHeaders(protocol, host, port, authorizationKey);
        ResponseEntity<String> response = mispAppClient.addMispEvent(event);
        return response;
    }


    // TODO: Test Organisations and Sharing Groups; (full CRUD API support for Sharing Groups still in investigation)

    @Test
    public void getMispOrganisationTest() throws URISyntaxException, IOException {
        ResponseEntity<String> responseEntity = mispAppClient.getMispOrganisation("56ef3277-1ad4-42f6-b90b-04e5c0a83832");
        LOG.info(responseEntity.getBody().toString());
        assertThat(responseEntity.getStatusCodeValue(), is(200));
    }

//  TODO: The value of the field "name" inside the resource file can't already exist in MISP when running the test. Fix it.
//  Even though UUID and ID is auto-generated, the field "name" must also be unique in order for this test to pass.
    @Test
    public void addMispOrganisationTest() throws URISyntaxException, IOException {
        ResponseEntity<String> response = postOrganisation();
        LOG.info(response.getBody().toString());
        assertThat(response.getStatusCodeValue(), is(200));
    }

    @Test
    public void updateMispOrganisationTestByUuid() throws URISyntaxException, IOException {
    }

    @Test
    public void updateMispOrganisationTestById() throws URISyntaxException, IOException {
    }

    @Test
    public void deleteMispOrganisationTest() throws URISyntaxException, IOException {

        // First, create an Organisation on MISP for our deletion test using the organisation resource file sample.
        ResponseEntity<String> postResponse = postOrganisation();

        // The Organisation's id field will be used as the key for the deletion.
        // To do that we need to extract that id out of the response entity object.
        String body = postResponse.getBody();
        JsonNode object = new ObjectMapper().readTree(body);
        JsonNode node = object.path("Organisation").get("id");
        String id = node.textValue();

        LOG.info(id);

        // Begin actual deletion procedure via our MispAppClient object
        mispAppClient.setProtocolHostPortHeaders(protocol, host, port, authorizationKey);
        ResponseEntity<String> response = mispAppClient.deleteMispOrganisation(id);

        LOG.info(response.toString());
        assertThat(response.getStatusCodeValue(), is(200));
        assertThat(response.getBody(), containsString("Organisation deleted"));

    }


    @Test
    public void getAllMispSharingGroupsTest() throws URISyntaxException, IOException {
    }

    private ResponseEntity<String> postOrganisation () throws URISyntaxException, IOException {
        URL url = getClass().getClassLoader().getResource("json/organisation.json");
        File file = new File(url.getFile());
        String fieldToModify = "description";

        String organisation = new String(Files.readAllBytes(Paths.get(url.toURI())));

        mispAppClient.setProtocolHostPortHeaders(protocol, host, port, authorizationKey);
        ResponseEntity<String> response = mispAppClient.addMispOrganisation(organisation);
        return response;
    }

}
