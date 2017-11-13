package com.intrasoft.csp.misp.tests.sandbox;

import com.intrasoft.csp.commons.model.DataParams;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.SharingParams;
import com.intrasoft.csp.misp.MispAdapterEmitterApplication;
import com.intrasoft.csp.misp.client.MispClient;
import com.intrasoft.csp.misp.client.config.MispClientConfig;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {MispAdapterEmitterApplication.class, MispClient.class, MispClientConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
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

    @Autowired
    @Qualifier("MispClient")
    MispClient mispClient;

    @Test
    public void testPost(){
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
        sharingParams.setTcId("\"\"");
        sharingParams.setTeamId("\"\"");

        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataParams(dataParams);
        integrationData.setSharingParams(sharingParams);
//        integrationData.setDataObject(loadJsonFromFile());

        integrationData.setDataType(IntegrationDataType.EVENT);
        mispClient.postIntegrationDataAdapter(integrationData);


    }
}
