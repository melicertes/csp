package com.intrasoft.csp.misp.tests.sandbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.misp.client.MispAppClient;
import com.intrasoft.csp.misp.client.config.MispAppClientConfig;
import com.intrasoft.csp.misp.commons.models.OrganisationDTO;
import com.intrasoft.csp.misp.commons.models.generated.SharingGroup;
import com.intrasoft.csp.misp.commons.models.generated.SharingGroupOrgItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
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
import java.util.*;

import static com.intrasoft.csp.misp.commons.utils.JsonObjectHandler.readField;
import static com.intrasoft.csp.misp.commons.utils.JsonObjectHandler.updateField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {MispAppClient.class, MispAppClientConfig.class},
        properties = {
                "misp.app.protocol:http",
                "misp.app.host:misp.dimitris.dk",
                "misp.app.port:80",
                "misp.app.authorization.key:JNqWBxfPiIywz7hUe58MyJf6sD5PrTVaGm7hTn6c",
                "spring.jackson.deserialization.unwrap-root-value=true"
        })

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
        ResponseEntity<Object> responseEntity = mispAppClient.getMispEvent("5a12bb50-fcb4-4345-9bae-619a9e459fec");
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


    /*
    * MISP Organisations tests section
    * */
    @Test
    public void getMispOrganisationByUuidTest() throws URISyntaxException, IOException {

        // This is our search key; Organisation should exist on our MISP instance
        String uuid = "5a3043f4-0444-4a2b-93ff-7754580897c0";

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

    @Test
    public void getAllMispOrganisationsTest() {
        List<OrganisationDTO> mispOrgList = mispAppClient.getAllMispOrganisations();
        assertThat(mispOrgList.size()>0, is(true));
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
        testDTO.setLocal(true);
        testDTO.setNationality("delete me");
        testDTO.setAlias("delete me");
        testDTO.setAnonymise(true);
        testDTO.setContacts("delete me");
        testDTO.setCreatedBy("delete me");
        testDTO.setDateCreated("delete me");
        testDTO.setLandingPage("delete me");
        testDTO.setSector("delete me");
        testDTO.setType("delete me");

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

//      TODO: Uncomment the assertions below when the Organisations API updating problem no longer exists.
//        assertThat(updateResponseDTO.isLocal(), is(addResponseDTO.isLocal()));
//        assertThat(updateResponseDTO.getDescription(), is(addResponseDTO.getDescription()));

    }

//  Creates and deletes an organisation
    @Test
    public void deleteMispOrganisationTest() throws URISyntaxException, IOException {

        // First, create an organisation with a random name.
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

    /*
    * MISP Sharing Groups tests section
    * */

    /*
    * Sharing Groups API test-branch tests
    * */

    @Test
    public void getMispSharingGroupByIdTest() throws URISyntaxException, IOException {
        String id = "11";
        String apiUrl = "http://192.168.56.50:80/sharing_groups/view";
        mispAppClient.setProtocolHostPortHeaders(protocol, host, port, authorizationKey);

        SharingGroup sharingGroup = mispAppClient.getMispSharingGroup(id);
            LOG.info(sharingGroup.toString());
        assertThat(sharingGroup.getId(), is(id));
        assertThat(sharingGroup.getUuid(), is("5a536fe6-0884-42b6-ab63-05a8c0a83832"));
    }

    // Should return null when Sharing Group id is not found
    @Test
    public void getMispSharingGroupByIdNotFoundReturnsNullTest() throws URISyntaxException, IOException {
        String id = "0";
        String apiUrl = "http://192.168.56.50:80/sharing_groups/view";
        mispAppClient.setProtocolHostPortHeaders(protocol, host, port, authorizationKey);

        SharingGroup sharingGroup = mispAppClient.getMispSharingGroup(id);
        assertNull(sharingGroup);
    }

    // Testing custom UUID support.
    @Test
    public void addMispSharingGroupCustomUuidShouldBeAcceptedTest() {

        String apiUrl = "http://192.168.56.50:80/sharing_groups/add";

        SharingGroup sharingGroup = new SharingGroup();
        sharingGroup.setName("test-" + RandomStringUtils.random(4,true,false));
        sharingGroup.setReleasability("TY");
        sharingGroup.setActive(true);
        sharingGroup.setDescription("DELETE ME");
        String uuid = UUID.randomUUID().toString();
        sharingGroup.setUuid(uuid);

        SharingGroup response = mispAppClient.addMispSharingGroup(sharingGroup);
        assertNotNull(response);
        assertThat(response.getName(), is(sharingGroup.getName()));
        assertThat(response.getUuid(), is(uuid));
    }

    // Testing Organisations definition support upon creation of Sharing Groups.
    // In this scenario we're defining 2 new Organisations in the Sharing Group; they should be created on the fly
    // with the first one having a custom UUID (not generated by the API).
    @Test
    public void addMispSharingGroupWithOrganisationsShouldReturnAllOrganisationsTest() {

        String apiUrl = "http://192.168.56.50:80/sharing_groups/add";

        SharingGroup sharingGroup = new SharingGroup();
        sharingGroup.setName("test-" + RandomStringUtils.random(4,true,false));
        sharingGroup.setReleasability("TY");
        sharingGroup.setDescription("DELETE ME");
        String uuid = UUID.randomUUID().toString();
        sharingGroup.setUuid(uuid);

        List<SharingGroupOrgItem> sharingGroupOrgItems = new ArrayList<>();
        sharingGroupOrgItems.add(new SharingGroupOrgItem());
        sharingGroupOrgItems.add(new SharingGroupOrgItem());

        // Defining a new organisation with a custom uuid;
        sharingGroupOrgItems.get(0).setOrganisation(new OrganisationDTO());
        sharingGroupOrgItems.get(0).setExtend(false);
        String orgAName = "NewOrg-" + RandomStringUtils.random(4,true,false);
        sharingGroupOrgItems.get(0).getOrganisation().setName(orgAName);
        String customUuid = UUID.randomUUID().toString();
        sharingGroupOrgItems.get(0).getOrganisation().setUuid(customUuid);
        // Defining a new organisation without specifying a UUID.
        // If organisation's name already exists, the organisation found with that uuid is added in the Sharing Group instead.
        sharingGroupOrgItems.get(1).setOrganisation(new OrganisationDTO());
        sharingGroupOrgItems.get(1).setExtend(true);
        String orgBName = "NewOrg-" + RandomStringUtils.random(4,true,false);
        sharingGroupOrgItems.get(1).getOrganisation().setName(orgBName);
        sharingGroup.setSharingGroupOrg(sharingGroupOrgItems);

        SharingGroup response = mispAppClient.addMispSharingGroup(sharingGroup);

        // The Sharing Group's response should now have the organisations defined earlier
        List<OrganisationDTO> organisations = response.getAllOrganisations();

        assertThat(organisations.size(), is(2));

        assertTrue(organisations.stream().filter(o -> o.getUuid().equals(customUuid))
                .filter(o -> o.getName().equals(orgAName)).findFirst().isPresent());

        assertTrue(organisations.stream().filter(o -> o.getName().equals(orgBName)).findFirst().isPresent());


    }


    // Returns true on successful deletion, false when retrying to delete what is not there.
    @Test
    public void deleteMispSharingGroupReturnsTrueWhenFoundRetrunsFalseWhenNotFound() {

        SharingGroup sharingGroup = createSharingGroupObject();
        sharingGroup = mispAppClient.addMispSharingGroup(sharingGroup);
        String id = sharingGroup.getId(); // id should now exist (autogenerated on MISP)
        String apiUrl = "http://192.168.56.50:80/sharing_groups/delete/" + id;

        assertTrue(mispAppClient.deleteMispSharingGroup(id));
        assertFalse(mispAppClient.deleteMispSharingGroup(id));
    }

/*
     Updating fields uuid, name, description, releasability, active, local, roaming
     Important/notable fields that the Sharing Groups API currently does not support updating:
          + Organisations defined in the Sharing Group
          + id, uuid, local, roaming
*/
    // Currently changes on fields uuid, local and roaming are ignored by the API, and therefore the test will fail.
    @Test
    public void updateMispSharingGroupAttributesTest() {

        String postString = "UPDATED";
        String postUuid = UUID.randomUUID().toString();

        SharingGroup sharingGroup = createSharingGroupObject();
        sharingGroup = mispAppClient.addMispSharingGroup(sharingGroup);

        sharingGroup.setModified(""); // should not be null, otherwise the sharing group won't be updated on MISP.
        sharingGroup.setUuid(postUuid);
        sharingGroup.setName(postString);
        sharingGroup.setReleasability(postString);
        sharingGroup.setDescription(postString);
        sharingGroup.setActive(true);
        sharingGroup.setLocal(true);
        sharingGroup.setRoaming(true);

        sharingGroup = mispAppClient.updateMispSharingGroup(sharingGroup);
        assertThat(sharingGroup.getName(), is(postString));
        assertThat(sharingGroup.getReleasability(), is(postString));
        assertThat(sharingGroup.getDescription(), is(postString));
        assertTrue(sharingGroup.isActive());
        assertThat(sharingGroup.getUuid(), is(postUuid));
        assertTrue(sharingGroup.isLocal());
        assertTrue(sharingGroup.isRoaming());

    }

    // This test will create a new sharing group with 3 new organisations defined on the fly and then
    // it will try to replace the existing organisations from the sharing group with many new ones.
    // Currently there is no support for modifying or removing existing organisations in a sharing group.
    @Test
    public void updateMispSharingGroupOrganisationsReplaceOrgsTest() {
        String postString = "UPDATED";
        String postUuid = UUID.randomUUID().toString();

        SharingGroup sharingGroup = createSharingGroupObject();
        List<SharingGroupOrgItem> sharingGroupOrgItems = createOrgObjectsForSharingGroup(3);
        sharingGroup.setSharingGroupOrg(sharingGroupOrgItems);

        sharingGroup = mispAppClient.addMispSharingGroup(sharingGroup);

        int newSize = 6;
        sharingGroup.setModified("");
        List<SharingGroupOrgItem> sharingGroupOrgItemsUpdate = createOrgObjectsForSharingGroup(newSize);
        sharingGroup.setSharingGroupOrg(sharingGroupOrgItemsUpdate);

        sharingGroup = mispAppClient.updateMispSharingGroup(sharingGroup);
        assertThat(sharingGroup.getSharingGroupOrg().size(), is(newSize));

    }

    /*
    * Mocking Sharing Groups API tests
    * */

    // Keep in mind that UUIDs on GET requests are not yet supported by the Sharing Groups API
    @Test
    public void getMispSharingGroupByUuidTest() throws URISyntaxException, IOException {

        String uuid = "5a536fe6-0884-42b6-ab63-05a8c0a83832";
        String apiUrl = "http://192.168.56.50:80/sharing_groups/view";

        mispAppClient.setProtocolHostPortHeaders(protocol, host, port, authorizationKey);

        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(retryRestTemplate).build();
        mockServer.expect(requestTo(apiUrl+"/"+uuid))
                .andRespond(MockRestResponseCreators.withSuccess
                        (FileUtils.readFileToString(new File(sharingGroupUrl.toURI()), Charset.forName("UTF-8"))
                                .getBytes(), MediaType.APPLICATION_JSON_UTF8));

        SharingGroup sharingGroup = mispAppClient.getMispSharingGroup(uuid);
        LOG.info(sharingGroup.toString());

        assertThat(sharingGroup.getUuid(), is(uuid));
        mockServer.verify();
    }

    // Tests if we're grabbing all the embedded objects from the json response like the creator Organisation,
    // the Sharing Group (along with its Organisations) and the Sharing Group Server.
    // The json mock file is updated to be identical with the new sharing groups api test branch responses.
    @Test
    public void getMispSharingGroupHasAllObjectsTest() throws URISyntaxException, IOException {
        String id = "11";
        String apiUrl = "http://192.168.56.50:80/sharing_groups/view";

        mispAppClient.setProtocolHostPortHeaders(protocol, host, port, authorizationKey);

        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(retryRestTemplate).build();
        mockServer.expect(requestTo(apiUrl+"/"+id))
                .andRespond(MockRestResponseCreators.withSuccess
                        (FileUtils.readFileToString(new File(sharingGroupUrl.toURI()), Charset.forName("UTF-8"))
                                .getBytes(), MediaType.APPLICATION_JSON_UTF8));

        SharingGroup sharingGroup = mispAppClient.getMispSharingGroup(id);
        LOG.info(sharingGroup.toString());

        // Creator Organisation
        assertThat(sharingGroup.getOrganisation().getUuid(), is("56ef3277-1ad4-42f6-b90b-04e5c0a83832"));
        assertThat(sharingGroup.getOrganisation().getName(), is("MISP"));
        // List of the Organisations in the Sharing Group
        assertThat(sharingGroup.getAllOrganisations().size(), is(3));
        assertThat(sharingGroup.getAllOrganisations().get(0).getUuid(), is("56ef3277-1ad4-42f6-b90b-04e5c0a83832"));
        // List of the Servers
        assertThat(sharingGroup.getSharingGroupServer().size(), is(1));
        assertThat(sharingGroup.getSharingGroupServer().get(0).getServer().get(0).getName().toLowerCase(), is("local instance"));

    }

    @Test
    public void addMispSharingGroupWithNameAndUuidTest() throws URISyntaxException, IOException {

        String apiUrl = "http://192.168.56.50:80/sharing_groups/add";

        // Instantiating a sharing group and adding some data; a random name and a random v4 uuid
        SharingGroup sg = new SharingGroup();
        sg.setName("test-" + RandomStringUtils.random(4,true,false));
        sg.setDescription("delete me");
        // Generating a v4 UUID
        UUID generatedUuid = UUID.randomUUID();
        sg.setUuid(generatedUuid.toString());

        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(retryRestTemplate).build();
        mockServer.expect(requestTo(apiUrl))
                .andRespond(MockRestResponseCreators.withSuccess
                        (FileUtils.readFileToString(new File(sharingGroupUrl.toURI()), Charset.forName("UTF-8"))
                                .getBytes(), MediaType.APPLICATION_JSON_UTF8));

        SharingGroup sgResponse = mispAppClient.addMispSharingGroup(sg);
        LOG.info(sgResponse.toString());
        assertTrue(sgResponse.getUuid().equals("a36c31f4-dad3-4f49-b443-e6d6333649b1"));
        mockServer.verify();

    }

    @Test
    public void updateMispSharingGroupTest() throws URISyntaxException, IOException {

        String apiUrl = "http://192.168.56.50:80/sharing_groups/edit";
        String uuid = "a36c31f4-dad3-4f49-b443-e6d6333649b1";

        // Instantiating a sharing group and populating its fields with data.
        SharingGroup sg = new SharingGroup();
        sg.setName("test-" + RandomStringUtils.random(4,true,false));
        sg.setDescription("delete me");
        sg.setId("66");
        sg.setUuid(uuid);

        // Mocking Setup
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(retryRestTemplate).build();
        mockServer.expect(requestTo(apiUrl+"/"+sg.getId()))
                .andRespond(MockRestResponseCreators.withSuccess
                        (FileUtils.readFileToString(new File(sharingGroupUrl.toURI()), Charset.forName("UTF-8"))
                                .getBytes(), MediaType.APPLICATION_JSON_UTF8));

        SharingGroup sgResponse = mispAppClient.updateMispSharingGroup(sg);
        LOG.info(sgResponse.toString());
        assertThat(sgResponse.getUuid(), is(sg.getUuid()));
        mockServer.verify();

    }

    // Although MISP's REST API for SGs supports this call, we will be mocking it because of the "uuid" field's absence.
    // List size should be equal to the number of sharing groups in the response mock file
    @Test
    public void getAllMispSharingGroupsTest() throws URISyntaxException, IOException {

        int sharingGroupsOnMockResponseFile = 3;

        String apiUrl = "http://192.168.56.50:80/sharing_groups";

        mispAppClient.setProtocolHostPortHeaders(protocol, host, port, authorizationKey);

/*
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(retryRestTemplate).build();
        mockServer.expect(requestTo(apiUrl))
                .andRespond(MockRestResponseCreators.withSuccess
                        (FileUtils.readFileToString(new File(allSharingGroupsUrl.toURI()), Charset.forName("UTF-8"))
                                .getBytes(), MediaType.APPLICATION_JSON_UTF8));
*/

        List<SharingGroup> sharingGroupsList = mispAppClient.getAllMispSharingGroups();

//        assertThat(sharingGroupsList.size(), is(sharingGroupsOnMockResponseFile));
//        mockServer.verify();
        assertTrue(sharingGroupsList.size() == 5);

    }

    @Test
    public void deleteSharingGroupTest() throws URISyntaxException, IOException {

        String id = "3";
        String apiUrl = "http://192.168.56.50:80/sharing_groups/delete/" + id;

        mispAppClient.setProtocolHostPortHeaders(protocol, host, port, authorizationKey);

        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(retryRestTemplate).build();
        mockServer.expect(requestTo(apiUrl))
                .andRespond(MockRestResponseCreators.withSuccess());


        assertTrue(mispAppClient.deleteMispSharingGroup(id));
        mockServer.verify();

    }

    // Creates new Organisation Objects for use with Sharing Groups tests
    private List<SharingGroupOrgItem> createOrgObjectsForSharingGroup(int size) {

        String orgNamePrefix = "TestOrg-";
        String orgStringDataPrefix = "ORIGINAL";
        SharingGroupOrgItem[] sgoiArr = new SharingGroupOrgItem[size];
        for (int i = 0; i < sgoiArr.length; i++) {
            sgoiArr[i] = new SharingGroupOrgItem();
            sgoiArr[i].setExtend(false);
            sgoiArr[i].setOrganisation(new OrganisationDTO());
            sgoiArr[i].getOrganisation().setName(orgNamePrefix + RandomStringUtils.random(4,true,false));
            sgoiArr[i].getOrganisation().setDescription(orgStringDataPrefix);
            sgoiArr[i].getOrganisation().setType(orgStringDataPrefix);
            sgoiArr[i].getOrganisation().setNationality(orgStringDataPrefix);
            sgoiArr[i].getOrganisation().setUuid(UUID.randomUUID().toString());
        }
        return Arrays.asList(sgoiArr);
    }

    // Creates a Sharing Group Object for testing purposes
    private SharingGroup createSharingGroupObject() {
        String originalName = "Test-" + RandomStringUtils.random(4, true, false);
        String preString = "ORIGINAL";

        SharingGroup sharingGroup = new SharingGroup();
        sharingGroup.setName(originalName);
        sharingGroup.setDescription(preString);
        sharingGroup.setReleasability(preString);
        sharingGroup.setActive(false);
        return sharingGroup;
    }


}
