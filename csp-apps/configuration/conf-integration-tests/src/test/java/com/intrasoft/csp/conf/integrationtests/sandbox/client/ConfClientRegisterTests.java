package com.intrasoft.csp.conf.integrationtests.sandbox.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.commons.exceptions.InvalidDataTypeException;
import com.intrasoft.csp.conf.client.ConfClient;
import com.intrasoft.csp.conf.client.config.ConfClientConfig;
import com.intrasoft.csp.conf.commons.context.ApiContextUrl;
import com.intrasoft.csp.conf.commons.exceptions.*;
import com.intrasoft.csp.conf.commons.model.*;
import com.intrasoft.csp.conf.commons.types.StatusResponseType;
import com.intrasoft.csp.conf.server.ConfApp;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.SerializationUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ConfApp.class, ConfClientConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "conf.server.port:8090",
                "conf.server.protocol:http",
                "conf.server.host:localhost",

                "conf.client.ssl.enabled:false",

                "conf.retry.backOffPeriod:5000",
                "conf.retry.maxAttempts:0"
        })
@ActiveProfiles("tests")
public class ConfClientRegisterTests implements ApiContextUrl {

    private static final Logger LOG = LoggerFactory.getLogger(ConfClientTest.class);

    @Autowired
    @Qualifier("ConfRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Autowired
    @Qualifier("confClient")
    ConfClient confClient;

    RegistrationDTO registrationDefaultData;

    @Before
    public void prepare() throws IOException, URISyntaxException {
        URL register_data = getClass().getClassLoader().getResource("register.json");
        String jsonString = FileUtils.readFileToString(new File(register_data.toURI()), Charset.forName("UTF-8"));
        registrationDefaultData = new ObjectMapper().readValue(jsonString, RegistrationDTO.class);
    }

    /**
     * Test for a invalid CSP with RegistrationIsUpdate=true
     */
    @Test
    public void invalidCspRegisterTest() {
        String cspId = "invalid";
        RegistrationDTO registrationDTO = registrationDefaultData;

        //set request
        registrationDTO.setRegistrationIsUpdate(true);

        try {
            ResponseDTO responseDTO = confClient.register(cspId, registrationDTO);
            fail("Expected CategoryCannotBeDeletedException");
        }catch (ConfException e){
            Assert.assertThat(e.getMessage(), containsString(StatusResponseType.API_INVALID_CSP_ENTRY.text()));
        }
    }


    /**
     * Test for a CSP that exists with RegistrationIsUpdate=false
     */
    @Test
    public void validCspInvalidUpdateFlagRegisterTest() {
        String cspId = "11111111-1111-1111-1111-111111111111";
        RegistrationDTO registrationDTO = registrationDefaultData;

        //set request
        registrationDTO.setRegistrationIsUpdate(false);

        try {
            ResponseDTO responseDTO = confClient.register(cspId, registrationDTO);
            fail("Expected CategoryCannotBeDeletedException");
        }catch (ConfException e){
            Assert.assertThat(e.getMessage(), containsString(StatusResponseType.API_REGISTER_NOT_UPDATABLE.text()));
        }
    }

    /**
     * Test for a CSP that exists with RegistrationIsUpdate=true, with invalid module name
     */
    @Test
    public void invalidModuleNameRegisterTest() {
        String cspId = "11111111-1111-1111-1111-111111111111";
        RegistrationDTO registrationDTO = registrationDefaultData;
        ModulesInfoDTO modulesInfoDTO = new ModulesInfoDTO();
        ModuleInfoDTO moduleInfoDTO = new ModuleInfoDTO();

        //set initial request
        registrationDTO.setRegistrationIsUpdate(true);

        //set invalid module name
        moduleInfoDTO = registrationDTO.getModuleInfo().getModules().get(0);
        moduleInfoDTO.setName("invalid");
        List<ModuleInfoDTO> modules = new ArrayList<>();
        modules.add(moduleInfoDTO);
        modulesInfoDTO.setModules(modules);
        registrationDTO.setModuleInfo(modulesInfoDTO);
        try {
            ResponseDTO responseDTO = confClient.register(cspId, registrationDTO);
            fail("Expected CategoryCannotBeDeletedException");
        }catch (InvalidModuleNameException e){
            Assert.assertThat(e.getMessage(), containsString(StatusResponseType.API_INVALID_MODULE_NAME.text()));
        }
    }

    /**
     * Test for a CSP that exists with RegistrationIsUpdate=true, with valid module name, invalid module fullname
     */
    @Test
    public void invalidModuleFullNameRegisterTest() {
        String cspId = "11111111-1111-1111-1111-111111111111";
        RegistrationDTO registrationDTO = registrationDefaultData;
        ModulesInfoDTO modulesInfoDTO = new ModulesInfoDTO();
        ModuleInfoDTO moduleInfoDTO = new ModuleInfoDTO();

        //set initial request
        registrationDTO.setRegistrationIsUpdate(true);

        //set valid module name & invalid module fullname
        moduleInfoDTO = registrationDTO.getModuleInfo().getModules().get(0);
        moduleInfoDTO.setName("module1");
        ModuleDataDTO moduleDataDTO = new ModuleDataDTO();
        moduleDataDTO.setFullName("invalid");
        moduleInfoDTO.setAdditionalProperties(moduleDataDTO);
        List<ModuleInfoDTO> modules = new ArrayList<>();
        modules.add(moduleInfoDTO);
        modulesInfoDTO.setModules(modules);
        registrationDTO.setModuleInfo(modulesInfoDTO);
        try {
            ResponseDTO responseDTO = confClient.register(cspId, registrationDTO);
            fail("Expected CategoryCannotBeDeletedException");
        }catch (InvalidModuleNameException e){
            Assert.assertThat(e.getMessage(), containsString(StatusResponseType.API_INVALID_MODULE_NAME.text()));
        }
    }


    /**
     * Test for a CSP that exists with RegistrationIsUpdate=true, with valid module name, valid module fullname, invalid module version
     */
    @Test
    public void invalidModuleVersionRegisterTest() {
        String cspId = "11111111-1111-1111-1111-111111111111";
        RegistrationDTO registrationDTO = registrationDefaultData;
        ModulesInfoDTO modulesInfoDTO = new ModulesInfoDTO();
        ModuleInfoDTO moduleInfoDTO = new ModuleInfoDTO();

        //set initial request
        registrationDTO.setRegistrationIsUpdate(true);

        //set valid module name, valid module fullname, invalid module version
        moduleInfoDTO = registrationDTO.getModuleInfo().getModules().get(0);
        moduleInfoDTO.setName("module1");
        ModuleDataDTO moduleDataDTO = new ModuleDataDTO();
        moduleDataDTO.setFullName("module1.1");
        moduleDataDTO.setVersion(0);
        moduleInfoDTO.setAdditionalProperties(moduleDataDTO);
        List<ModuleInfoDTO> modules = new ArrayList<>();
        modules.add(moduleInfoDTO);
        modulesInfoDTO.setModules(modules);
        registrationDTO.setModuleInfo(modulesInfoDTO);
        try {
            ResponseDTO responseDTO = confClient.register(cspId, registrationDTO);
            fail("Expected CategoryCannotBeDeletedException");
        }catch (InvalidModuleVersionException e){
            Assert.assertThat(e.getMessage(), containsString(StatusResponseType.API_INVALID_MODULE_VERSION.text()));
        }
    }


    /**
     * Test for a CSP that exists with RegistrationIsUpdate=true, with valid module name, valid module fullname, valid module version, invalid module hash
     */
    @Test
    public void invalidModuleHashRegisterTest() {
        String cspId = "11111111-1111-1111-1111-111111111111";
        RegistrationDTO registrationDTO = registrationDefaultData;
        ModulesInfoDTO modulesInfoDTO = new ModulesInfoDTO();
        ModuleInfoDTO moduleInfoDTO = new ModuleInfoDTO();

        //set initial request
        registrationDTO.setRegistrationIsUpdate(true);

        //set valid module name, valid module fullname, valid module version, invalid module hash
        moduleInfoDTO = registrationDTO.getModuleInfo().getModules().get(0);
        moduleInfoDTO.setName("module1");
        ModuleDataDTO moduleDataDTO = new ModuleDataDTO();
        moduleDataDTO.setFullName("module1.1");
        moduleDataDTO.setVersion(10000);
        moduleDataDTO.setHash("invalid");
        moduleInfoDTO.setAdditionalProperties(moduleDataDTO);
        List<ModuleInfoDTO> modules = new ArrayList<>();
        modules.add(moduleInfoDTO);
        modulesInfoDTO.setModules(modules);
        registrationDTO.setModuleInfo(modulesInfoDTO);
        try {
            ResponseDTO responseDTO = confClient.register(cspId, registrationDTO);
            fail("Expected CategoryCannotBeDeletedException");
        }catch (InvalidModuleHashException e){
            Assert.assertThat(e.getMessage(), containsString(StatusResponseType.API_INVALID_MODULE_HASH.text()));
        }
    }


    /**
     * Test for a CSP that exists with RegistrationIsUpdate=true, with valid module data
     */
    @Test
    public void validIsUpdateRegisterTest() {
        String cspId = "11111111-1111-1111-1111-111111111111";
        RegistrationDTO registrationDTO = registrationDefaultData;
        ModulesInfoDTO modulesInfoDTO = new ModulesInfoDTO();
        ModuleInfoDTO moduleInfoDTO = new ModuleInfoDTO();

        //set initial request
        registrationDTO.setRegistrationIsUpdate(true);

        //set valid module name, valid module fullname, valid module version, invalid module hash
        moduleInfoDTO = registrationDTO.getModuleInfo().getModules().get(0);
        moduleInfoDTO.setName("module1");
        ModuleDataDTO moduleDataDTO = new ModuleDataDTO();
        moduleDataDTO.setFullName("module1.1");
        moduleDataDTO.setVersion(10000);
        moduleDataDTO.setHash("fd61127757973c982cec9d15b61da61a173c8ea86c3122655b92abacec5c7edacff933f896a895d50ecfa3c8f9fc34eb76258bbc228fdf35a5b767a9b1a4c9");
        moduleDataDTO.setInstalledOn("2017-04-24T11:59:35Z");
        moduleInfoDTO.setAdditionalProperties(moduleDataDTO);
        List<ModuleInfoDTO> modules = new ArrayList<>();
        modules.add(moduleInfoDTO);
        modulesInfoDTO.setModules(modules);
        registrationDTO.setModuleInfo(modulesInfoDTO);

        ResponseDTO responseDTO = confClient.register(cspId, registrationDTO);
        Assert.assertThat(responseDTO.getResponseCode(), equalTo(StatusResponseType.OK.code()));

    }



    /**
     * Test for a new CSP registration
     */
    @Test
    public void validRegisterTest() {
        String cspId = UUID.randomUUID().toString();
        RegistrationDTO registrationDTO = registrationDefaultData;
        ModulesInfoDTO modulesInfoDTO = new ModulesInfoDTO();
        ModuleInfoDTO moduleInfoDTO = new ModuleInfoDTO();

        //set initial request
        registrationDTO.setRegistrationIsUpdate(false);

        //set valid module name, valid module fullname, valid module version, invalid module hash
        moduleInfoDTO = registrationDTO.getModuleInfo().getModules().get(0);
        moduleInfoDTO.setName("module1");
        ModuleDataDTO moduleDataDTO = new ModuleDataDTO();
        moduleDataDTO.setFullName("module1.1");
        moduleDataDTO.setVersion(10000);
        moduleDataDTO.setHash("fd61127757973c982cec9d15b61da61a173c8ea86c3122655b92abacec5c7edacff933f896a895d50ecfa3c8f9fc34eb76258bbc228fdf35a5b767a9b1a4c9");
        moduleDataDTO.setInstalledOn("2017-04-24T11:59:35Z");
        moduleInfoDTO.setAdditionalProperties(moduleDataDTO);
        List<ModuleInfoDTO> modules = new ArrayList<>();
        modules.add(moduleInfoDTO);
        modulesInfoDTO.setModules(modules);
        registrationDTO.setModuleInfo(modulesInfoDTO);

        ResponseDTO responseDTO = confClient.register(cspId, registrationDTO);
        Assert.assertThat(responseDTO.getResponseCode(), equalTo(StatusResponseType.OK.code()));

    }


}
