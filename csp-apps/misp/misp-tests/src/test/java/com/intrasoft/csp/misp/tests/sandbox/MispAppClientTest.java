package com.intrasoft.csp.misp.tests.sandbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.misp.client.MispAppClient;
import com.intrasoft.csp.misp.client.config.MispAppClientConfig;
import com.intrasoft.csp.misp.commons.models.OrganisationDTO;
import com.intrasoft.csp.misp.commons.models.SharingGroupDTO;
import com.intrasoft.csp.misp.commons.models.generated.Organisation;
import com.intrasoft.csp.server.utils.TestUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.HttpClientErrorException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static com.intrasoft.csp.misp.commons.utils.JsonObjectHandler.readField;
import static com.intrasoft.csp.misp.commons.utils.JsonObjectHandler.updateField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {MispAppClient.class, MispAppClientConfig.class},
        properties = {
                "misp.app.protocol:http",
                "misp.app.host:192.168.56.50",
                "misp.app.port:80",
                "misp.app.authorization.key:JNqWBxfPiIywz7hUe58MyJf6sD5PrTVaGm7hTn6c",
                "spring.jackson.deserialization.unwrap-root-value=true"
        })
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

    @Autowired
    @Qualifier("MispAppRestTemplate")
    RetryRestTemplate retryRestTemplate;

    URL sharingGroupUrl = getClass().getClassLoader().getResource("json/sharingGroup.json");
    URL allSharingGroupsUrl = getClass().getClassLoader().getResource("json/allSharingGroups.json");

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


    // TODO: Test Organisations and Sharing Groups; (Waiting for full CRUD API support for Sharing Groups)

    @Test
    public void getMispOrganisationByUuidTest() throws URISyntaxException, IOException {

        // This is our search key; Organisation should exist on our MISP instance
        String uuid = "56ef3277-1ad4-42f6-b90b-04e5c0a83832";

        OrganisationDTO organisationDTO = mispAppClient.getMispOrganisation(uuid);
        LOG.info(organisationDTO.toString());
        assertThat(organisationDTO.getUuid(), is(uuid));

    }

    @Test
    public void getMispOrganisationNotFoundShouldReturnNullTest() throws URISyntaxException, IOException {

        // This is our search key. Organisation should NOT exist on our MISP instance
        String uuid = "39049c69-8355-47c3-86e3-929b77373aff";

        assertNull(mispAppClient.getMispOrganisation(uuid));

    }



//  Adding an organisation in MISP using the fields "name" and a uuid. Both values must be unique.
    @Test
    public void addMispOrganisationTest() throws URISyntaxException, IOException {

        // This organisation object should be added in our MISP instance.
        OrganisationDTO testDTO = new OrganisationDTO();

        // Unlike uuids and ids in MISP, the field "name" is not autogenerated; it must be provided and also be unique.
        // Otherwise, exception 403 Forbidden is thrown and our organisation DTO doesn't get to be written.

        // Setting the mandatory field's "name" value as "test-" plus some random string to prevent possible collisions.
        testDTO.setName("test-" + RandomStringUtils.random(4,true,false));
        testDTO.setDescription("delete me");

        // Generating a v4 UUID implementation (MISP recommends UUID v4) for our test organisation.
        UUID generatedUuid = UUID.randomUUID();
        testDTO.setUuid(generatedUuid.toString());

        mispAppClient.setProtocolHostPortHeaders(protocol, host, port, authorizationKey);
        OrganisationDTO responseDTO = mispAppClient.addMispOrganisation(testDTO);

        LOG.info(responseDTO.toString());
        assertThat(testDTO.getName(), is(responseDTO.getName()));
        assertThat(testDTO.getUuid(), is(responseDTO.getUuid()));
    }

    //  Adding an organisation in MISP with a name that already exists should throw an exception.
    @Test(expected = HttpClientErrorException.class)
    public void addMispOrganisationDuplicateNameThrowsExceptionTest() throws URISyntaxException, IOException {

        OrganisationDTO testDTO = new OrganisationDTO();

        // This organisation name should already exist in our MISP instance in order for the test to be successful.
        testDTO.setName("CIRCL");

        mispAppClient.setProtocolHostPortHeaders(protocol, host, port, authorizationKey);
        OrganisationDTO responseDTO = mispAppClient.addMispOrganisation(testDTO);

    }

    // Creating a dummy organisation for update testing purposes
    @Test
    public void updateMispOrganisationTest() throws URISyntaxException, IOException {

        // First, create an organisation with a random name in our MISP instance.
        OrganisationDTO testDTO = new OrganisationDTO();
        testDTO.setName("test-" + RandomStringUtils.random(4,true,false));
        testDTO.setDescription("delete me");

        mispAppClient.setProtocolHostPortHeaders(protocol, host, port, authorizationKey);
        OrganisationDTO addResponseDTO = mispAppClient.addMispOrganisation(testDTO);

        // Change some fields and try updating it.
        String updateString = " updated";
        addResponseDTO.setName(addResponseDTO.getName() + updateString);
        addResponseDTO.setLocal(true);
        addResponseDTO.setDescription(addResponseDTO.getDescription() + updateString);

        // Misp App client will need to get hold of the MISP-generated id in order to update the organisation.
        // The response DTO should provide it.
        OrganisationDTO updateResponseDTO = mispAppClient.updateMispOrganisation(addResponseDTO);

        assertThat(updateResponseDTO.getName(), is(addResponseDTO.getName()));

//      TODO: These should be successful but they're not because of the Organisations REST API updating problem.
        assertThat(updateResponseDTO.isLocal(), is(addResponseDTO.isLocal()));
        assertThat(updateResponseDTO.getDescription(), is(addResponseDTO.getDescription()));

    }

//  Create an organisation for the deletion test
    @Test
    public void deleteMispOrganisationTest() throws URISyntaxException, IOException {

        // First, create a dummy organisation with a random name.
        OrganisationDTO testDTO = new OrganisationDTO();
        testDTO.setName("test-" + RandomStringUtils.random(4,true,false));
        testDTO.setDescription("delete me");

        mispAppClient.setProtocolHostPortHeaders(protocol, host, port, authorizationKey);
        OrganisationDTO addResponseDTO = mispAppClient.addMispOrganisation(testDTO);

        // Misp App client will need to get hold of the MISP-generated id in order to delete the organisation.
        // The add response DTO should provide it.
        String id = addResponseDTO.getId();


        assertTrue(mispAppClient.deleteMispOrganisation(id));
        LOG.info("Deleted Organisation " + addResponseDTO.getName() + " with and ID of " + addResponseDTO.getId());

    }

    @Test
    public void getMispSharingGroupByUuidTest() throws URISyntaxException, IOException {


        String uuid = "a36c31f4-dad3-4f49-b443-e6d6333649b1";
        String apiUrl = "http://192.168.56.50:80/sharing_groups/view";


        mispAppClient.setProtocolHostPortHeaders(protocol, host, port, authorizationKey);

        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(retryRestTemplate).build();
        mockServer.expect(requestTo(apiUrl+"/"+uuid))
                .andRespond(MockRestResponseCreators.withSuccess(FileUtils.readFileToString(new File(sharingGroupUrl.toURI()), Charset.forName("UTF-8")).getBytes(), MediaType.APPLICATION_JSON_UTF8));






/*
        mockServer.expect(requestTo(apiUrl+"/"+uuid))
                .andRespond(MockRestResponseCreators
                        .withSuccess(TcMockUtil.getJsonBytesForTeamByUuid(allTeams,uuid),TestUtil.APPLICATION_JSON_UTF8));


        //test client
        Team team = tcClient.getTeamByUuid(uuid);
        assertThat(team.getId(),is(uuid));
        assertThat(team.getHostOrganisation(),is("delete me"));
        mockServer.verify();
*/
        assertTrue(true);
        mockServer.verify();
    }


    @Test
    public void getAllMispSharingGroupsTest() throws URISyntaxException, IOException {
    }

/*
    private ResponseEntity<String> postOrganisation () throws URISyntaxException, IOException {
        URL url = getClass().getClassLoader().getResource("json/organisation.json");
        File file = new File(url.getFile());
        String fieldToModify = "description";

        String organisation = new String(Files.readAllBytes(Paths.get(url.toURI())));


        mispAppClient.setProtocolHostPortHeaders(protocol, host, port, authorizationKey);
        ResponseEntity<String> response = mispAppClient.addMispOrganisation(organisation);
        return response;
    }
*/

}
