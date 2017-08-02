package com.intrasoft.csp.integration.sandbox.client.csp;

import com.intrasoft.csp.client.CspClient;
import com.intrasoft.csp.commons.model.*;
import com.intrasoft.csp.commons.routes.ContextUrl;
import com.intrasoft.csp.integration.MockUtils;
import com.intrasoft.csp.libraries.versioning.client.ApiVersionClient;
import com.intrasoft.csp.libraries.versioning.model.VersionDTO;
import com.intrasoft.csp.server.CspApp;
import com.intrasoft.csp.server.service.CamelRestService;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

/**
 * Created by iskitsas on 5/22/17.
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {CspApp.class,MockUtils.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "server.port: 8082",
                "server.ssl.enabled:true",
                "server.ssl.key-store=classpath:sslcert/csp-internal.jks",
                "server.ssl.key-store-password:123456",
                "server.ssl.key-password:123456",
                "server.ssl.client-auth:need",
                "server.ssl.allow.all.hostname:true",
                "csp.server.protocol: https",
                "csp.client.ssl.enabled: true",
                "csp.client.ssl.jks.keystore: classpath:sslcert/csp-internal.jks",
                "csp.client.ssl.jks.keystore.password: 123456",
                "csp.server.host: localhost",
                "csp.server.port: 8082",
                "api.version: 1",
                "csp.retry.backOffPeriod:10",
                "csp.retry.maxAttempts:1",
                "embedded.activemq.start:false",
                "apache.camel.use.activemq:false",
        })
@MockEndpointsAndSkip("http:*")
public class CspSSLTest implements ContextUrl {
    @Autowired
    ApiVersionClient apiVersionClient;

    @Autowired
    CspClient cspClient;

    @MockBean
    CamelRestService camelRestService;

    @Autowired
    MockUtils mockUtils;

    @Before
    public void init() throws IOException {
        Mockito.when(camelRestService.send(anyString(), anyObject(), eq("GET"), eq(TrustCircle.class)))
                .thenReturn(mockUtils.getMockedTrustCircle(3));
        Mockito.when(camelRestService.send(anyString(), anyObject(), eq("GET"), eq(Team.class)))
                .thenReturn(mockUtils.getMockedTeam(1,"http://external.csp%s.com"))
                .thenReturn(mockUtils.getMockedTeam(2,"http://external.csp%s.com"))
                .thenReturn(mockUtils.getMockedTeam(3,"http://external.csp%s.com"));
        Mockito.when(camelRestService.sendAndGetList(anyString(), anyObject(), eq("GET"), eq(TrustCircle.class), anyObject()))
                .thenReturn(mockUtils.getAllMockedTrustCircles(3, IntegrationDataType.tcNamingConventionForShortName.get(IntegrationDataType.INCIDENT)));

    }

    @Test
    public void cspApiVersionTest(){
        VersionDTO versionDTO = apiVersionClient.getApiVersion();
        String apiUrl = apiVersionClient.getApiUrl();
        String cspContext = apiVersionClient.getContext();
        assertThat(versionDTO.getMaxVersion(),is(1.0));
        assertThat(apiUrl,is("https://localhost:8082/v1"));
        assertThat(cspContext,is("/v1"));
    }

    @Test
    public void sendPostIntegrationDataTest() throws IOException {
        IntegrationData integrationData = new IntegrationData();
        DataParams dataParams = new DataParams("cspId","applicationId","recordId"
                ,DateTime.parse("2014-12-13 09:30:17", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")),
                "originCspId","originAppId","originRecId");
        integrationData.setDataParams(dataParams);
        integrationData.setDataType(IntegrationDataType.INCIDENT);
        SharingParams sharingParams = new SharingParams();
        sharingParams.setIsExternal(false);
        sharingParams.setToShare(true);
        integrationData.setDataObject("something");
        integrationData.setSharingParams(sharingParams);
        ResponseEntity<String> response = cspClient.postIntegrationData(integrationData, DSL_INTEGRATION_DATA);
        assertThat(response.getStatusCode(),is(HttpStatus.OK));
    }
}
