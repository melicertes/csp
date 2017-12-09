package com.intrasoft.csp.misp.tests.sandbox;

import com.intrasoft.csp.client.TrustCirclesClient;
import com.intrasoft.csp.client.config.TrustCirclesClientConfig;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.misp.client.MispAppClient;
import com.intrasoft.csp.misp.client.config.MispAppClientConfig;
import com.intrasoft.csp.misp.commons.models.OrganisationDTO;
import com.intrasoft.csp.misp.config.MispTcSyncServiceConfig;
import com.intrasoft.csp.misp.service.MispTcSyncService;
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
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;


@RunWith(SpringJUnit4ClassRunner.class)

@SpringBootTest( classes = {MispTcSyncService.class, MispTcSyncServiceConfig.class, TrustCirclesClient.class,
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
    URL allTrustCircles = getClass().getClassLoader().getResource("json/allTrustCircles.json");
    URL sharingGroupUrl = getClass().getClassLoader().getResource("json/sharingGroup.json");
    URL allSharingGroupsUrl = getClass().getClassLoader().getResource("json/allSharingGroups.json");

    //Description
    @Test
    public void syncOrganisationsScenarioATest() {
        List<OrganisationDTO> orgList = mispAppClient.getAllMispOrganisations();
    }
    //Description
    @Test
    public void syncOrganisationsScenarioBTest() {

    }
    //Description
    @Test
    public void syncSharingGroupsScenarioATest() {

    }
    //Description
    @Test
    public void syncSharingGroupsScenarioBTest() {

    }
    //Description
    @Test
    public void syncAllScenarioATest() {

    }
    //Description
    @Test
    public void syncAllScenarioBTest() {

    }




/*    //  TODO: Delete this

    public void basicAutowiringTest() throws IOException, URISyntaxException {

        //mock the TC server using json retrieved from a real TC
        String apiUrl = tcConfig.getTcTeamsURI();
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(tcRetryRestTemplate).build();
        mockServer.expect(requestTo(apiUrl))
                .andRespond(MockRestResponseCreators
                        .withSuccess(FileUtils.readFileToString(new File(allTeams.toURI()), Charset.forName("UTF-8"))
                                .getBytes(), MediaType.APPLICATION_JSON_UTF8));


        //mock the misp server using json retrieved from a real TC
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(retryRestTemplate).build();
        mockServer.expect(requestTo(apiUrl))
                .andRespond(MockRestResponseCreators.withSuccess
                        (FileUtils.readFileToString(new File(allSharingGroupsUrl.toURI()), Charset.forName("UTF-8"))
                                .getBytes(), MediaType.APPLICATION_JSON_UTF8));



//        assertThat(mispTcSyncService.getTeams(),is(6));
        mockServer.verify();
    }*/

}
