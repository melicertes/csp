package com.intrasoft.csp.misp.tests.sandbox;

import com.intrasoft.csp.client.TrustCirclesClient;
import com.intrasoft.csp.client.config.TrustCirclesClientConfig;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.misp.client.MispAppClient;
import com.intrasoft.csp.misp.client.config.MispAppClientConfig;
import com.intrasoft.csp.misp.commons.models.OrganisationDTO;
import com.intrasoft.csp.misp.commons.models.generated.SharingGroup;
import com.intrasoft.csp.misp.service.MispTcSyncService;
import com.intrasoft.csp.misp.service.impl.MispTcSyncServiceImpl;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;


@RunWith(SpringJUnit4ClassRunner.class)

@SpringBootTest( classes = {MispTcSyncServiceImpl.class, TrustCirclesClient.class,
                            TrustCirclesClientConfig.class, MispAppClient.class, MispAppClientConfig.class},
        properties = {
                "misp.app.protocol:http",
                "misp.app.host:192.168.56.50",
                "misp.app.port:80",
                "misp.app.authorization.key:JNqWBxfPiIywz7hUe58MyJf6sD5PrTVaGm7hTn6c",
                "spring.jackson.deserialization.unwrap-root-value=true"
        }
)

public class MispTcSyncServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(MispTcSyncService.class);

    @Autowired
    MispTcSyncService mispTcSyncService;

    @Autowired
//  @Qualifier("MispAppClient")
    MispAppClient mispAppClient;

    @Autowired
    TrustCirclesClientConfig tcConfig;

    @Autowired
    @Qualifier("TcRestTemplate")
    RetryRestTemplate tcRetryRestTemplate;

    @Autowired
    @Qualifier("MispAppRestTemplate")
    RetryRestTemplate mispRetryRestTemplate;

    // Necessary mock response files
    URL allTeams = getClass().getClassLoader().getResource("json/allTeams.json");
    URL twoTeams = getClass().getClassLoader().getResource("json/twoTeams.json");
    URL allTrustCircles = getClass().getClassLoader().getResource("json/allTrustCircles.json");
    URL twoTrustCircles = getClass().getClassLoader().getResource("json/twoTrustCircles.json");
    URL twoTrustCirclesUpdated = getClass().getClassLoader().getResource("json/twoTrustCirclesUpdated.json");
    URL sharingGroup = getClass().getClassLoader().getResource("json/sharingGroup.json");
    URL allSharingGroups = getClass().getClassLoader().getResource("json/allSharingGroups.json");
    URL organisation = getClass().getClassLoader().getResource("json/organisation.json");

    // Service should synchronize Trust Circles' Teams with MISP's Organisations.
    // TODO: Organisations in MISP can't be deleted when tied with users or events. UI Response Message:
    // "Organisation could not be deleted. Generally organisations should never be deleted, instead consider moving them
    // to the known remote organisations list. Alternatively, if you are certain that you would like to remove an
    // organisation and are aware of the impact, make sure that there are no users or events still tied to this
    // organisation before deleting it."
    // Given 6 TC Teams, service should create all 6 of them (assuming they don't already exist)
    // Also tests for the existence of the name prefix after synchronizing
    @Test
    public void syncOrganisationsSixTeamsShouldAddSixOrgsTest() throws URISyntaxException, IOException {

        //mock the TC server using json retrieved from a real TC (6 teams in file)

        String apiUrl = tcConfig.getTcTeamsURI();
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(tcRetryRestTemplate).build();
        mockServer.expect(requestTo(apiUrl))
                .andRespond(MockRestResponseCreators
                        .withSuccess(FileUtils.readFileToString(new File(allTeams.toURI()), Charset.forName("UTF-8"))
                                .getBytes(), MediaType.APPLICATION_JSON_UTF8));

        mispTcSyncService.syncOrganisations();

        List<String> uuidList = Arrays.asList("306de7b8-5e8c-4a5e-9de2-1f837713bfc1", "974b5557-9aca-468c-90cc-961b31df0ef6",
                "578c0e4e-ebaf-455b-a2a1-faffb14be9e1", "af9d06ac-d7be-4684-86a3-808fe4f4d17c",
                "88557939-db1c-4411-831a-9b6226ef4819", "6a552fd8-100c-4c3f-8b0f-5ec1d6bf009d");

        uuidList.forEach(uuid -> {
            assertThat(uuid, is(mispAppClient.getMispOrganisation(uuid).getUuid()));
            assertTrue(mispAppClient.getMispOrganisation(uuid).getName().startsWith("CSP::"));
        });

        mockServer.verify();
    }
    // When two TC teams exist, their two corresponding MISP organisations should only be left in MISP after synchronizing.
    // (This test will now fail because we've removed the organisation deletion from the synchronization logic.)
    @Test
    public void syncOrganisationsTwoTeamsShouldSyncTwoOrgsTest() throws URISyntaxException, IOException {

        String apiUrl = tcConfig.getTcTeamsURI();
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(tcRetryRestTemplate).build();
        mockServer.expect(requestTo(apiUrl))
                .andRespond(MockRestResponseCreators
                        .withSuccess(FileUtils.readFileToString(new File(twoTeams.toURI()), Charset.forName("UTF-8"))
                                .getBytes(), MediaType.APPLICATION_JSON_UTF8));

        mispTcSyncService.syncOrganisations();

        assertThat(mispAppClient.getAllMispOrganisations().size(), is(2));
        mockServer.verify();

    }

    // This is the scenario where two Trust Circles don't already exist in MISP and need to be created,
    // while the Teams referenced in one of the Trust Circles already exist in MISP as Organisations.
    // Trust Circle A has no Teams while Trust Circle B has two.
    @Test
    public void syncSharingGroupsNonExistingSharingGroupsTest() throws URISyntaxException, IOException {
        String tcCirclesURI = tcConfig.getTcCirclesURI();
        String mispGroupsURI = "http://192.168.56.50:80/sharing_groups";
        String mispAddGroupURI = "http://192.168.56.50:80/sharing_groups/add";
        String sharingGroupUuid = "a36c31f4-dad3-4f49-b443-e6d6333649b1";

        // We first need to mock TC server's getAllTrustCircles response
        MockRestServiceServer tcMockServer = MockRestServiceServer.bindTo(tcRetryRestTemplate).build();
        tcMockServer.expect(requestTo(tcCirclesURI))
                .andRespond(MockRestResponseCreators
                        .withSuccess(FileUtils.readFileToString(new File(twoTrustCircles.toURI()),
                                Charset.forName("UTF-8")).getBytes(), MediaType.APPLICATION_JSON_UTF8));

        mispTcSyncService.syncSharingGroups();

        // Assert that the Trust Circles corresponding Sharing Groups exist.
        List<SharingGroup> sharingGroups = mispAppClient.getAllMispSharingGroups();
        // Temporary fix for unknown Sharing Group UUIDs API issue; making extra GET calls to fetch them
        sharingGroups.forEach(sharingGroup ->  {
            sharingGroup.setUuid(mispAppClient.getMispSharingGroup(sharingGroup.getId()).getUuid());
        });
        List<String> tcUuids = Arrays.asList("2883f242-3e07-4378-9091-0d198e4886ba", "61ee0197-587f-43ce-afcf-310b36b5bfe9");

        tcUuids.forEach(uuid -> {
            assertTrue(sharingGroups.stream().anyMatch(sharingGroup -> sharingGroup.getUuid().equals(uuid)));
        });

        // The Sharing Groups Organisation content should match the Trust Circles Team content.
        // Sharing Group A should be empty, while Sharing Group B should reference 2 Organisations
        SharingGroup sharingGroupA = sharingGroups.stream().filter(sg -> sg.getUuid().equals(tcUuids.get(0))).findFirst().get();
        SharingGroup sharingGroupB = sharingGroups.stream().filter(sg -> sg.getUuid().equals(tcUuids.get(1))).findFirst().get();
        assertThat(sharingGroupA.getSharingGroupOrg().size()-1, is(0));  // -1: (adds user's Organisation by default)
        assertThat(sharingGroupB.getSharingGroupOrg().size(), is(2));


        // Organisation content on Sharing Group B
        assertTrue(sharingGroupB.getSharingGroupOrg().stream().anyMatch(sharingGroupOrgItem ->
            sharingGroupOrgItem.getOrganisation().getName().equals("CSP::demo1-csp")));
        assertTrue(sharingGroupB.getSharingGroupOrg().stream().anyMatch(sharingGroupOrgItem ->
                sharingGroupOrgItem.getOrganisation().getName().equals("CSP::demo2-csp")));

        tcMockServer.verify();
//      mispMockServer.verify();
    }

    // TODO: The scenario where some of the TC' Trust Circles already exist in MISP and need to be updated.
    // Since we're not currently able to update the Sharing Group's Organisations content via the API,
    // a temporary solution is deleting and re-creating the Sharing Group with the updated Organisation content.
    @Test
    public void syncSharingGroupsExistingSharingGroupsTest() throws URISyntaxException, IOException {
        String tcCirclesURI = tcConfig.getTcCirclesURI();
        String mispGroupsURI = "http://192.168.56.50:80/sharing_groups";
        String mispAddGroupURI = "http://192.168.56.50:80/sharing_groups/add";
        String sharingGroupUuid = "a36c31f4-dad3-4f49-b443-e6d6333649b1";

        // We first need to mock TC server's getAllTrustCircles response
        MockRestServiceServer tcMockServer = MockRestServiceServer.bindTo(tcRetryRestTemplate).build();
        tcMockServer.expect(requestTo(tcCirclesURI))
                .andRespond(MockRestResponseCreators
                        .withSuccess(FileUtils.readFileToString(new File(twoTrustCirclesUpdated.toURI()),
                                Charset.forName("UTF-8")).getBytes(), MediaType.APPLICATION_JSON_UTF8));

        // create the existing organisation here manually
        String newOrgUuid = "9a74c807-e6c4-4a19-9c77-37457e3285df";
        List<String> tcUuids = Arrays.asList("2883f242-3e07-4378-9091-0d198e4886ba", "61ee0197-587f-43ce-afcf-310b36b5bfe9");
        String[] orgUuids = {newOrgUuid, "578c0e4e-ebaf-455b-a2a1-faffb14be9e1", newOrgUuid};
        OrganisationDTO organisationDTO = new OrganisationDTO();
        organisationDTO.setName("DELETE ME");
        organisationDTO.setUuid(newOrgUuid);
        mispAppClient.addMispOrganisation(organisationDTO);

        mispTcSyncService.syncSharingGroups();

        // Assert that the Trust Circles corresponding Sharing Groups exist.
        List<SharingGroup> sharingGroups = mispAppClient.getAllMispSharingGroups();
        // Temporary fix for unknown Sharing Group UUIDs API issue; making extra GET calls to fetch them
        sharingGroups.forEach(sharingGroup ->  {
            sharingGroup.setUuid(mispAppClient.getMispSharingGroup(sharingGroup.getId()).getUuid());
        });

        // The Sharing Groups Organisation content should match the Trust Circles Team content.
        // Sharing Group A should have one Organisation defined, while Sharing Group B should have two.
        SharingGroup sharingGroupA = sharingGroups.stream().filter(sg -> sg.getUuid().equals(tcUuids.get(0))).findFirst().get();
        SharingGroup sharingGroupB = sharingGroups.stream().filter(sg -> sg.getUuid().equals(tcUuids.get(1))).findFirst().get();
        assertThat(sharingGroupA.getSharingGroupOrg().size(), is(0));
        assertThat(sharingGroupB.getSharingGroupOrg().size(), is(2));

        // Organisation content verification
        assertTrue(sharingGroupB.getSharingGroupOrg().stream().anyMatch(sharingGroupOrgItem ->
                sharingGroupOrgItem.getOrganisation().getName().equals("DELETE ME")));
        assertTrue(sharingGroupB.getSharingGroupOrg().stream().anyMatch(sharingGroupOrgItem ->
                sharingGroupOrgItem.getOrganisation().getName().equals("CSP::demo1-csp")));
        assertTrue(sharingGroupB.getSharingGroupOrg().stream().anyMatch(sharingGroupOrgItem ->
                sharingGroupOrgItem.getOrganisation().getName().equals("DELETE ME")));



        tcMockServer.verify();


    }
    // TODO: The scenario where some Sharing Groups in MISP have no corresponding Trust Circles.
    // Should all be deleted
    public void syncSharingGroupsMarkOrphansPassiveTest() {

    }
    @Test
    public void syncAllScenarioATest() {

    }
    //Description
    @Test
    public void syncAllScenarioBTest() {

    }


}
