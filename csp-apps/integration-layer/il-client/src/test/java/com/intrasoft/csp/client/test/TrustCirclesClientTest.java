package com.intrasoft.csp.client.test;

import com.intrasoft.csp.client.TrustCirclesClient;
import com.intrasoft.csp.client.config.TrustCirclesClientConfig;
import com.intrasoft.csp.client.test.util.TcMockUtil;
import com.intrasoft.csp.commons.model.Contact;
import com.intrasoft.csp.commons.model.Team;
import com.intrasoft.csp.commons.model.TrustCircle;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.server.utils.TestUtil;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {TrustCirclesClientConfig.class},
        properties = {
                "tc.retry.backOffPeriod:10",//ms
                "tc.retry.maxAttempts:1",
                "tc.protocol:http",
                "tc.host:localhost",
                "tc.port:8081"})
public class TrustCirclesClientTest {
    private static final Logger LOG = LoggerFactory.getLogger(TrustCirclesClientTest.class);

    @Autowired
    RetryRestTemplate retryRestTemplate;

    @Autowired
    TrustCirclesClient tcClient;

    @Autowired
    TrustCirclesClientConfig tcConfig;

    URL allTeams = getClass().getClassLoader().getResource("json/tc/allTeams.json");
    URL allTrustCircles = getClass().getClassLoader().getResource("json/tc/allTrustCircles.json");
    URL allLocalTrustCircles = getClass().getClassLoader().getResource("json/tc/allLocalTrustCircles.json");
    URL allContacts = getClass().getClassLoader().getResource("json/tc/allContacts.json");


    @Test
    public void getAllTrustCirclesTest() throws IOException, URISyntaxException {
        //mock the TC server using json retrieved from a real TC
        String apiUrl = tcConfig.getTcCirclesURI();
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(retryRestTemplate).build();
        mockServer.expect(requestTo(apiUrl))
                .andRespond(MockRestResponseCreators
                        .withSuccess(TcMockUtil.getJsonBytesFromUrl(allTrustCircles),TestUtil.APPLICATION_JSON_UTF8));


        //test client
        List<TrustCircle> list = tcClient.getAllTrustCircles();
        assertThat(list.size(),is(13));
        mockServer.verify();
    }

    @Test
    public void getAllTeamsTest() throws IOException, URISyntaxException {
        //mock the TC server using json retrieved from a real TC
        String apiUrl = tcConfig.getTcTeamsURI();
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(retryRestTemplate).build();
        mockServer.expect(requestTo(apiUrl))
                .andRespond(MockRestResponseCreators
                        .withSuccess(TcMockUtil.getJsonBytesFromUrl(allTeams),TestUtil.APPLICATION_JSON_UTF8));


        //test client
        List<Team> list = tcClient.getAllTeams();
        assertThat(list.size(),is(6));
        mockServer.verify();
    }

    @Test
    public void getTrustCircleByUuidTest() throws IOException, URISyntaxException {
        String uuid = "2883f242-3e07-4378-9091-0d198e4886ba";
        //mock the TC server using json retrieved from a real TC
        String apiUrl = tcConfig.getTcCirclesURI();
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(retryRestTemplate).build();
        mockServer.expect(requestTo(apiUrl+"/"+uuid))
                .andRespond(MockRestResponseCreators
                        .withSuccess(TcMockUtil.getJsonBytesForTrustCircleByUuid(allTrustCircles,uuid),TestUtil.APPLICATION_JSON_UTF8));


        //test client
        TrustCircle tc = tcClient.getTrustCircleByUuid(uuid);
        assertThat(tc.getId(),is(uuid));
        mockServer.verify();
    }

    @Test
    public void getTeamByUuidTest() throws IOException, URISyntaxException {
        String uuid = "974b5557-9aca-468c-90cc-961b31df0ef6";
        //mock the TC server using json retrieved from a real TC
        String apiUrl = tcConfig.getTcTeamsURI();
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(retryRestTemplate).build();
        mockServer.expect(requestTo(apiUrl+"/"+uuid))
                .andRespond(MockRestResponseCreators
                        .withSuccess(TcMockUtil.getJsonBytesForTeamByUuid(allTeams,uuid),TestUtil.APPLICATION_JSON_UTF8));


        //test client
        Team team = tcClient.getTeamByUuid(uuid);
        assertThat(team.getId(),is(uuid));
        assertThat(team.getHostOrganisation(),is("delete me"));
        mockServer.verify();
    }

    @Test
    public void getAllLocalTrustCirclesTest() throws IOException, URISyntaxException {
        //mock the TC server using json based on LTC Specifications document
        String apiUrl = tcConfig.getTcLocalCircleURI();
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(retryRestTemplate).build();
        mockServer.expect(requestTo(apiUrl))
                .andRespond(MockRestResponseCreators
                        .withSuccess(TcMockUtil.getJsonBytesFromUrl(allLocalTrustCircles),TestUtil.APPLICATION_JSON_UTF8));


        //test client
        List<TrustCircle> list = tcClient.getAllLocalTrustCircles();
        assertThat(list.size(),is(3));
        mockServer.verify();
    }

    @Test
    public void getLocalTrustCircleByUuidTest() throws IOException, URISyntaxException {
        String uuid = "31146113-d53d-4738-877d-2405ea18edf8";
        //mock the TC server using json based on LTC Specifications document
        String apiUrl = tcConfig.getTcLocalCircleURI();
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(retryRestTemplate).build();
        mockServer.expect(requestTo(apiUrl+"/"+uuid))
                .andRespond(MockRestResponseCreators
                        .withSuccess(TcMockUtil.getJsonBytesForTrustCircleByUuid(allLocalTrustCircles,uuid),TestUtil.APPLICATION_JSON_UTF8));

        //test client
        TrustCircle tc = tcClient.getLocalTrustCircleByUuid(uuid);
        assertThat(tc.getId(),is(uuid));
        mockServer.verify();
    }

    @Test
    public void getLocalTrustcircleByShortNameTest() throws IOException, URISyntaxException {
        String shortName = "LTC shortname B";
        String queryParam = "short_name";
        String apiUrl = tcConfig.getTcLocalCircleURI();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl).queryParam(queryParam, shortName);
        apiUrl = builder.toUriString();
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(retryRestTemplate).build();
        mockServer.expect(requestTo(apiUrl))
                .andRespond(MockRestResponseCreators
                        .withSuccess(TcMockUtil.getJsonBytesForLTCByShortName(allLocalTrustCircles, shortName),TestUtil.APPLICATION_JSON_UTF8));

        //test client
        TrustCircle tc = tcClient.getLocalTrustCircleByShortName(shortName);
        assertThat(tc.getShortName(), is(shortName));
        mockServer.verify();
    }

    @Test
    public void getContactByIdTest() throws IOException, URISyntaxException {
        String id = "a1a2876b-cbb2-4cbd-b99d-340e6e4ffb34";
        String shortName = "Mocked B";
        String email = "userB@example.com";

        //mock the TC server using json based on LTC Specifications document
        String apiUrl = tcConfig.getTcContactsURI();
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(retryRestTemplate).build();
        mockServer.expect(requestTo(apiUrl+"/"+id))
                .andRespond(MockRestResponseCreators
                        .withSuccess(TcMockUtil.getJsonBytesForContactById(allContacts,id),TestUtil.APPLICATION_JSON_UTF8));

        // test client
        Contact contact = tcClient.getContactById(id);
        assertThat(contact.getId(), is(id));
        assertThat(contact.getShortName(), is(shortName));
        assertThat(contact.getEmail(), is(email));

        mockServer.verify();
    }

}
