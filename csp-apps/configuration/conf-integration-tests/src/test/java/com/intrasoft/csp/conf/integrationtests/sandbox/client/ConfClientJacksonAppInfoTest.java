package com.intrasoft.csp.conf.integrationtests.sandbox.client;

import com.intrasoft.csp.conf.commons.model.api.AppInfoDTO;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;


@JsonTest
@RunWith(SpringRunner.class)
@SpringBootTest(classes={ConfClientJacksonAppInfoTest.class})
public class ConfClientJacksonAppInfoTest {

    @Autowired
    private JacksonTester<AppInfoDTO> regDtoJson;


    @Test
    public void testJsonMarshalling() throws Exception {
        String json = IOUtils.toString(this.getClass().getResourceAsStream("/appInfo.json"), "UTF-8");
        final AppInfoDTO appInfoDTO = regDtoJson.parse(json).getObject();

        Assert.assertTrue("json should have 1 module", appInfoDTO.getModulesInfo().getModules().size() == 1);

    }

}