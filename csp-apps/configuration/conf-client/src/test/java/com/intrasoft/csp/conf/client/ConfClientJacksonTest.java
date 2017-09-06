package com.intrasoft.csp.conf.client;

import com.intrasoft.csp.conf.commons.model.RegistrationDTO;
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


/**
 * Created by tangelatos on 06/09/2017.
 */
@JsonTest
@RunWith(SpringRunner.class)
@SpringBootTest(classes={ConfClientJacksonTest.class})
public class ConfClientJacksonTest {
    @Autowired
    private JacksonTester<RegistrationDTO> regDtoJson;


    @Test
    public void testJsonMarshalling() throws Exception {
        String json = IOUtils.toString(this.getClass().getResourceAsStream("/register/register-valid-1.json"), "UTF-8");
        final RegistrationDTO registrationDTO = regDtoJson.parse(json).getObject();

        Assert.assertTrue("json registration should have update = true", registrationDTO.getRegistrationIsUpdate() == true);
        Assert.assertTrue("json should have 2 contacts", registrationDTO.getContacts().size() == 2);
        Assert.assertEquals("json should have contacts with type tech", registrationDTO.getContacts().stream()
                .map( contactDetailsDTO -> contactDetailsDTO.getContactType()).distinct().findAny().get(), ContactType.TECH_ADMIN);
    }

}
