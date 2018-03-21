package com.intrasoft.csp.regrep.esclient.test;

import com.intrasoft.csp.client.CspDataMappingType;
import com.intrasoft.csp.client.ElasticSearchClient;
import com.intrasoft.csp.client.config.ElasticSearchClientConfig;
import com.intrasoft.csp.client.impl.ElasticSearchClientImpl;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ElasticSearchClient.class, ElasticSearchClientConfig.class})
public class RegularReportsEsClientTest {

    private static final Logger LOG = LoggerFactory.getLogger(RegularReportsEsClientTest.class);

    URL elasticResponse = getClass().getClassLoader().getResource("json.es/response.json");

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ElasticSearchClient elasticSearchClient;

    @Test
    public void getNdocsByTypeTest() throws URISyntaxException, IOException {
        String apiUrl = "http://docker.containers:9200/cspdata/artefact/_count";
        MockRestServiceServer mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
        mockRestServiceServer.expect(requestTo(apiUrl))
                .andRespond(MockRestResponseCreators.withSuccess(FileUtils.readFileToString(new File(elasticResponse.toURI()), Charset.forName("UTF-8"))
                        .getBytes(), MediaType.APPLICATION_JSON_UTF8));
        int response = elasticSearchClient.getNdocsByType(CspDataMappingType.ARTEFACT, "test" );
        assertTrue(response==9);
        mockRestServiceServer.verify();
    }



}
