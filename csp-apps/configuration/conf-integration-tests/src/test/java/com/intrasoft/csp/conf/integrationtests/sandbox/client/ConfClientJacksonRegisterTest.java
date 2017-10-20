package com.intrasoft.csp.conf.integrationtests.sandbox.client;

import com.intrasoft.csp.conf.commons.model.api.RegistrationDTO;
import com.intrasoft.csp.conf.commons.types.ContactType;
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
@SpringBootTest(classes={ConfClientJacksonRegisterTest.class})
public class ConfClientJacksonRegisterTest {
    @Autowired
    private JacksonTester<RegistrationDTO> regDtoJson;


    @Test
    public void testJsonMarshalling() throws Exception {
        String json = IOUtils.toString(this.getClass().getResourceAsStream("/register.json"), "UTF-8");
        final RegistrationDTO registrationDTO = regDtoJson.parse(json).getObject();

        Assert.assertTrue("json registration should have update = true", registrationDTO.getRegistrationIsUpdate() == true);
        Assert.assertTrue("json should have 2 contacts", registrationDTO.getContacts().size() == 2);
        Assert.assertTrue("json should have 2 internal IPs", registrationDTO.getInternalIPs().size() == 2);
        Assert.assertTrue("json should have 2 external IPs", registrationDTO.getExternalIPs().size() == 2);
        Assert.assertTrue("json should have 1 module", registrationDTO.getModuleInfo().getModules().size() == 1);
        Assert.assertEquals("json should have contacts with type tech", registrationDTO.getContacts().stream()
                .map( contactDetailsDTO -> contactDetailsDTO.getContactType()).distinct().findAny().get(), ContactType.TECH_ADMIN);
    }

}