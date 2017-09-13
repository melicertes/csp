package com.intrasoft.csp.server.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.commons.model.DataParams;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.SharingParams;
import com.intrasoft.csp.server.CspApp;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CspApp.class,MockUtils.class},
        properties = {
                "spring.datasource.url:jdbc:h2:mem:csp_policy",
                "flyway.enabled:false",
                "csp.retry.backOffPeriod:10",
                "csp.retry.maxAttempts:1",
                "embedded.activemq.start:false",
                "apache.camel.use.activemq:false",
        })
public class DataMappingTest {
    private static final Logger LOG = LoggerFactory.getLogger(DataMappingTest.class);
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockUtils mockUtils;

    @Test
    public void outputJsonDataObjectTest() throws IOException {
        String tcId = "justATcId";
        String teamId = "justATeamId";
        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataType(IntegrationDataType.INCIDENT);

        DataParams dataParams = new DataParams();
        dataParams.setApplicationId("test1");
        dataParams.setCspId("testCspId");
        dataParams.setRecordId("recordId");
        dataParams.setOriginCspId("origin-testCspId");
        dataParams.setOriginApplicationId("origin-test1");
        dataParams.setOriginRecordId("origin-recordId");
        dataParams.setDateTime(DateTime.now());
        dataParams.setUrl("http://rt.cert-gr.melecertes.eu/Ticket/Display.html?id=23453");
        SharingParams sharingParams = new SharingParams();
        sharingParams.setIsExternal(true);
        sharingParams.setToShare(true);
        if(!StringUtils.isEmpty(tcId)){
            sharingParams.setTcId(tcId);
        }
        if(!StringUtils.isEmpty(teamId)){
            sharingParams.setTeamId(teamId);
        }
        integrationData.setDataParams(dataParams);
        String data = mockUtils.getDataObjectMap().get(integrationData.getDataType());
        JsonNode jsonNode = objectMapper.readTree(data);
        integrationData.setDataObject(jsonNode);
        integrationData.setSharingParams(sharingParams);

        String out = objectMapper.writeValueAsString(integrationData);
        LOG.info(out);
        assertThat(out,containsString("{\"incident\":{\"event\":{\"classification.identifier\":\"heartbleed\",\"classification.taxonomy\":\"attack on the critical infrastructure\",\"classification.type\":\"command and control servers for example\",\"comment\":\"Very serious issue\",\"destination.abuse_contact\":\"Mr Bill Gates\",\"destination.account\":\"abuse@microsoft.com\",\"destination.allocated\":\"1487827718\",\"destination.as_name\":\"System Name\",\"destination.asn\":123654789,\"destination.fqdn\":\"www.microsoft.com\",\"destination.geolocation.cc\":\"US\",\"destination.geolocation.city\":\"Seattle\",\"destination.geolocation.country\":\"United States\",\"destination.geolocation.latitude\":32.543234,\"destination.geolocation.longitude\":24.654321,\"destination.geolocation.region\":\"Americas\",\"destination.geolocation.state\":\"Washington\",\"destination.ip\":\"127.0.0.1\",\"destination.local_hostname\":\"hostname\",\"destination.local_ip\":\"192.168.1.1\",\"destination.network\":\"701 1239 42 206.24. 14.0/24\",\"destination.port\":22,\"destination.registry\":\"IP registry code\",\"destination.reverse_dns\":\"www.microsoft.com\",\"destination.tor_node\":false,\"destination.url\":\"http://somephishingsite.com\",\"event_description.target\":\"ENISA\",\"event_description.text\":\"A very serious attack against ENISA\",\"event_description.url\":\"https://www.enisa.europa.eu/\",\"event_hash\":\"13da502ab0d75daca5e5075c60e81bfe3b7a637f\",\"extra\":\" {inlinejson:{}}\",\"feed.accuracy\":95,\"feed.code\":\"HSDAG\",\"feed.documentation\":\"https://www.enisa.europa.eu/topics/trust-services\",\"feed.name\":\"SomeFeedName\",\"feed.provider\":\"SomeCSIRT\",\"feed.url\":\"https://www.enisa.europa.eu/topics/trust-services\",\"malware.hash.md5\":\"9f2520a3056543d49bb0f822d85ce5dd\",\"malware.hash.sha1\":\"13da502ab0d75daca5e5075c60e81bfe3b7a637f\",\"malware.hash.sha256\":\"2d79fcc6b02a2e183a0cb30e0e25d103f42badda9fbf86bbee06f93aa3855aff\",\"malware.name\":\"NSA HD firmware hacks\",\"malware.version\":\"crime-ware kit\",\"misp.attribute_uuid\":\"586cb1ff-6bcc-4029-88b0-4fa9950d210f\",\"misp.event_uuid\":\"586cb1ff-6bcc-4029-88b0-4fa9950d210f\",\"protocol.application\":\"ssh\",\"protocol.transport\":\"tcp\",\"raw\":\"U29tZSBiaW5hcnkgYmxvYg==\",\"rtir_id\":123456,\"screenshot_url\":\"https://www.enisa.europa.eu/logo.png\",\"source.abuse_contact\":\"abuse@enisa.eu, abuse@microsoft.com\",\"source.account\":\"source@abuse.com\",\"source.allocated\":\"1487827718\",\"source.as_name\":\"Some System Name\",\"source.asn\":123654789,\"source.fqdn\":\"www.microsoft.com\",\"source.geolocation.cc\":\"US\",\"source.geolocation.city\":\"New York\",\"source.geolocation.country\":\"United States\",\"source.geolocation.cymru_cc\":\"US\",\"source.geolocation.geoip_cc\":\"US\",\"source.geolocation.latitude\":28.543234,\"source.geolocation.longitude\":26.543234,\"source.geolocation.region\":\"Americas\",\"source.geolocation.state\":\"New York\",\"source.ip\":\"192.168.1.1\",\"source.local_hostname\":\"myhost\",\"source.local_ip\":\"192.168.1.2\",\"source.network\":\"701 1239 42 206.24. 14.0/24\",\"source.port\":1088,\"source.registry\":\"this IP registry code\",\"source.reverse_dns\":\"www.microsoft.com\",\"source.tor_node\":true,\"source.url\":\"https://somesite.org\",\"status\":\"offline\",\"time.observation\":\"1487827718\",\"time.source\":\"1487827715\"},\"report\":{\"feed.accuracy\":56.5,\"feed.code\":\"DFGS\",\"feed.documentation\":\"https://www.enisa.europa.eu/topics/trust-services\",\"feed.name\":\"MyFeedName\",\"feed.provider\":\"Some CSIRT\",\"feed.url\":\"https://www.enisa.europa.eu/\",\"raw\":\"U29tZSBiaW5hcnkgYmxvYg==\",\"rtir_id\":123654789,\"time.observation\":\"1487827715\"}"));

    }

    @Test
    public void outputJsonSharingParamsTest() throws JsonProcessingException {
        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataType(IntegrationDataType.INCIDENT);
        SharingParams sharingParams = new SharingParams();
        sharingParams.setTcId("dummyTcId");
        integrationData.setSharingParams(sharingParams);
        String out = objectMapper.writeValueAsString(integrationData);
        LOG.info(out);
        assertThat(out,containsString("\"trustCircleId\":\"dummyTcId\""));
    }

    @Test
    public void inputJsonSharingParamsTest() throws IOException {
        String inStr = "{\"dataParams\":null,\"sharingParams\":{\"toShare\":null,\"isExternal\":null,\"trustCircleId\":\"dummyTcId\",\"teamId\":null},\"dataType\":\"incident\",\"dataObject\":null}";
        IntegrationData integrationData = objectMapper.readValue(inStr,IntegrationData.class);
        assertThat(integrationData.getSharingParams().getTcId(), is("dummyTcId"));
    }
}
