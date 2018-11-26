package com.intrasoft.csp.regrep.esclient.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.regrep.CspDataMappingType;
import com.intrasoft.csp.regrep.DateMath;
import com.intrasoft.csp.regrep.ElasticSearchClient;
import com.intrasoft.csp.regrep.LogstashMappingType;
import com.intrasoft.csp.regrep.commons.model.HitsItem;
import com.intrasoft.csp.regrep.config.ElasticSearchClientConfig;
import com.intrasoft.csp.regrep.service.RequestBodyService;
import com.intrasoft.csp.regrep.service.impl.RequestBodyServiceImpl;
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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ElasticSearchClient.class, ElasticSearchClientConfig.class, RequestBodyServiceImpl.class})
public class RegularReportsEsClientTest {

    private static final Logger LOG = LoggerFactory.getLogger(RegularReportsEsClientTest.class);

    URL elasticResponse = getClass().getClassLoader().getResource("json.es/response.json");
    URL dailyExcLogsResponse = getClass().getClassLoader().getResource("json.es/exc-response.json");

    @Autowired
    RetryRestTemplate restTemplate;

    @Autowired
    ElasticSearchClient elasticSearchClient;

    @Autowired
    RequestBodyService requestBodyService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void getNlogsTest() throws URISyntaxException, IOException {
        String apiUrl = "http://docker.containers:9200/logstash*/_count";
        MockRestServiceServer mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
        mockRestServiceServer.expect(requestTo(apiUrl))
                .andRespond(MockRestResponseCreators.withSuccess(FileUtils.readFileToString(new File(elasticResponse.toURI()), Charset.forName("UTF-8"))
                        .getBytes(), MediaType.APPLICATION_JSON_UTF8));
        int response = elasticSearchClient.getNlogs("test" );
        assertTrue(response==9);
        mockRestServiceServer.verify();
    }

    @Test
    public void getNlogsByTypeTest() {
        String apiUrl = "http://docker.containers:9200/logstash*/_count";
        String requestBody;
        requestBody = requestBodyService.buildRequestBody(new String(), new String(), LogstashMappingType.EXCEPTION);
        LOG.info(requestBody);
        int count = elasticSearchClient.getNlogs(requestBody);
    }

    @Test
    public void getNdocsByTypeTest() {
        String apiUrl = "http://docker.containers:9200/cspdata/_count";
        String requestBody;
        requestBody = requestBodyService.buildRequestBody(new String(), new String(), CspDataMappingType.ALL);
        LOG.info(requestBody);
        int count = elasticSearchClient.getNdocs(requestBody);
    }

    // Mocking test using data taken from an actual es response
    @Test
    public void getLogDataShouldReturnExpectedResultsTest() throws URISyntaxException, IOException {
        String apiUrl = "http://docker.containers:9200/logstash*/_search";
        MockRestServiceServer mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
        mockRestServiceServer.expect(requestTo(apiUrl))
                .andRespond(MockRestResponseCreators.withSuccess(FileUtils.readFileToString(new File(dailyExcLogsResponse.toURI()), Charset.forName("UTF-8"))
                        .getBytes(), MediaType.APPLICATION_JSON_UTF8));

        String hitSearchId = "AWLb3DlK65cDyhNhdJY5";
        String expectedTimestamp = "2018-04-19T03:04:21.007Z";
        String expectedMessage = "2018-04-19 03:04:20 csp-misp-adapter.local.demo1-csp.athens.intrasoft-intl.private" +
                " CSP.MISP-ADAPTER[38]: Servlet.service() for servlet [dispatcherServlet] in context with path [] threw" +
                " exception [Request processing failed; nested exception is java.lang.NullPointerException] with root cause";
        String expectedProgram = "CSP.MISP-ADAPTER";

        List<HitsItem> hitsItemList= elasticSearchClient.getLogData("test" );

        assertTrue(hitsItemList.size()==10);

        assertTrue(hitsItemList.stream().filter(hitsItem -> hitsItem.getId().equals(hitSearchId))
                .filter(hitsItem -> hitsItem.getSource().getTimestamp().equals(expectedTimestamp))
                .filter(hitsItem -> hitsItem.getSource().getMessage().equals(expectedMessage))
                .anyMatch(hitsItem -> hitsItem.getSource().getProgram().equals(expectedProgram)));

        mockRestServiceServer.verify();
    }


}
