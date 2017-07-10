package com.intrasoft.csp.integration.sandbox.server.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.commons.model.DataParams;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.SharingParams;
import com.intrasoft.csp.server.CspApp;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by iskitsas on 5/23/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CspApp.class})
public class CspJodaDateTimeFormatTest {
    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void DataParamsJodaDateTimeTest() throws JsonProcessingException {
        DateTime dateTime = DateTime.parse("2014-12-13 09:30:17", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).withZone(DateTimeZone.UTC);
        DateTime dateTimeOtherFormat = DateTime.parse("2014-12-13T07:30:17+0000", DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ")).withZone(DateTimeZone.UTC);
        IntegrationData integrationData = new IntegrationData();
        DataParams dataParams = new DataParams("cspId","applicationId","recordId", dateTime,"originCspId","originAppId","originRecId");
        integrationData.setDataParams(dataParams);
        integrationData.setDataType(IntegrationDataType.INCIDENT);
        SharingParams sharingParams = new SharingParams();
        sharingParams.setIsExternal(false);
        sharingParams.setToShare(true);
        integrationData.setSharingParams(sharingParams);
        String jsonData = objectMapper.writeValueAsString(integrationData);

        assertThat(jsonData, containsString(dateTimeOtherFormat.toString("yyyy-MM-dd'T'HH:mm:ssZ")));

    }
}
