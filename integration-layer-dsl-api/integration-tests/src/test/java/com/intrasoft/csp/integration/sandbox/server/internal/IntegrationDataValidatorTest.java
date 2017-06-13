package com.intrasoft.csp.integration.sandbox.server.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.commons.model.DataParams;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.SharingParams;
import com.intrasoft.csp.commons.validators.IntegrationDataValidator;
import com.intrasoft.csp.server.CspApp;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by iskitsas on 6/13/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CspApp.class})
public class IntegrationDataValidatorTest {
    @Autowired
    IntegrationDataValidator integrationDataValidator;

    @Autowired
    ObjectMapper objectMapper;

    final String[] validJsonStrArr= {"{\"t\":\"1234\"}"};
    final String[] invalidJsonStrArr = {"bad json"};

    @Test
    public void validIntegrationDataTest(){

    }

    @Test
    public void invalidIntegrationDataTest(){

    }

    @Test
    public void validDataObjectJsonStringArrTest() throws IOException {

        for(String json: validJsonStrArr){
            IntegrationData integrationData = getIntegrationDataMockObject(false);
            integrationData.setDataObject(objectMapper.readValue(json,Object.class));
            BindingResult bindingResult = new BeanPropertyBindingResult(integrationData,"integrationData");
            integrationDataValidator.validate(integrationData,bindingResult);
            assertThat(bindingResult.hasErrors(), is(false));
        }
    }

    @Test
    public void invalidDataObjectJsonStringArrTest() throws IOException {
        for(String json: invalidJsonStrArr){
            IntegrationData integrationData = getIntegrationDataMockObject(false);
            integrationData.setDataObject(objectMapper.readValue(json,Object.class));
            BindingResult bindingResult = new BeanPropertyBindingResult(integrationData,"integrationData");
            integrationDataValidator.validate(integrationData,bindingResult);
            assertThat(bindingResult.hasErrors(), is(true));
            assertThat(bindingResult.getAllErrors().size(),is(1));
            assertThat(bindingResult.getAllErrors().get(0).toString(),containsString("IntegrationData.dataObject is not a valid json"));
        }
    }

    private IntegrationData getIntegrationDataMockObject(boolean hasValidDataObject){
        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataType(IntegrationDataType.ARTEFACT);
        SharingParams sharingParams = new SharingParams();
        sharingParams.setIsExternal(true);
        sharingParams.setToShare(false);
        integrationData.setSharingParams(sharingParams);
        DataParams dataParams = new DataParams();
        dataParams.setRecordId("222");
        dataParams.setApplicationId("applicationId");
        dataParams.setDateTime(DateTime.now());
        dataParams.setCspId("cspId");
        integrationData.setDataParams(dataParams);
        if(hasValidDataObject) {
            integrationData.setDataObject("{\"t\":\"1234\"}");
        }
        return integrationData;
    }
}
