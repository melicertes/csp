package com.intrasoft.csp.integration.business.client.elastic;


import com.intrasoft.csp.client.ElasticClient;
import com.intrasoft.csp.client.config.CspClientConfig;
import com.intrasoft.csp.client.config.ElasticClientConfig;
import com.intrasoft.csp.client.config.TrustCirclesClientConfig;
import com.intrasoft.csp.commons.model.DataParams;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.SharingParams;
import com.intrasoft.csp.server.CspApp;
import com.intrasoft.csp.server.utils.MockUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {CspApp.class, ElasticClientConfig.class},
        properties = {
                "elastic.host:csp0.dangerduck.gr",
                "elastic.path:",
                "elastic.protocol:http",
                "elastic.port:9200",
                "flyway.enabled:false"
        })
public class ElasticClientSandboxTest {

    @Autowired
    ElasticClient elasticClient;

    @Test
    public void testElastic() throws IOException, URISyntaxException {

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

        boolean exists = elasticClient.objectExists(integrationData);
        assertThat(exists, is(false));
    }

    public String loadJsonFromFile() throws URISyntaxException, IOException {
        URL url = getClass().getClassLoader().getResource("json/data_threat.json");
        File file = new File(url.getFile());
        String event = new String(Files.readAllBytes(Paths.get(url.toURI())));
        return event;
    }

}
