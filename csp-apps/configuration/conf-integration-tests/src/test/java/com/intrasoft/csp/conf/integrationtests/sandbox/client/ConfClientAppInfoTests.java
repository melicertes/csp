package com.intrasoft.csp.conf.integrationtests.sandbox.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.conf.client.ConfClient;
import com.intrasoft.csp.conf.client.config.ConfClientConfig;
import com.intrasoft.csp.conf.commons.context.ApiContextUrl;
import com.intrasoft.csp.conf.commons.exceptions.InvalidCspEntryException;
import com.intrasoft.csp.conf.commons.exceptions.InvalidModuleHashException;
import com.intrasoft.csp.conf.commons.exceptions.InvalidModuleNameException;
import com.intrasoft.csp.conf.commons.exceptions.InvalidModuleVersionException;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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
public class ConfClientAppInfoTests implements ApiContextUrl {

    private static final Logger LOG = LoggerFactory.getLogger(ConfClientTest.class);

    @Autowired
    @Qualifier("ConfRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Autowired
    @Qualifier("confClient")
    ConfClient confClient;

    AppInfoDTO appInfoDefaultData;

    @Before
    public void prepare() throws IOException, URISyntaxException {
        URL appInfo_data = getClass().getClassLoader().getResource("appInfo.json");
        String jsonString = FileUtils.readFileToString(new File(appInfo_data.toURI()), Charset.forName("UTF-8"));
        appInfoDefaultData = new ObjectMapper().readValue(jsonString, AppInfoDTO.class);
    }

    /**
     * Test for a CSP that does not exist
     */
    @Test
    public void invalidCspAppInfoTest() {
        String cspId = "invalid";
        AppInfoDTO appInfoDTO = this.appInfoDefaultData;

        try {
            ResponseDTO responseDTO = confClient.appInfo(cspId, appInfoDTO);
            fail("Expected CategoryCannotBeDeletedException");
        }catch (InvalidCspEntryException e){
            Assert.assertThat(e.getMessage(), containsString(StatusResponseType.API_INVALID_CSP_ENTRY.text()));
        }
    }

    /**
     * Test for a CSP that exists with RegistrationIsUpdate=true, with invalid module name
     */
    @Test
    public void invalidModuleNameAppInfoTest() {
        String cspId = "11111111-1111-1111-1111-111111111111";
        AppInfoDTO appInfoDTO = this.appInfoDefaultData;
        ModulesInfoDTO modulesInfoDTO = new ModulesInfoDTO();
        ModuleInfoDTO moduleInfoDTO;


        //set invalid module name
        moduleInfoDTO = appInfoDTO.getModulesInfo().getModules().get(0);
        moduleInfoDTO.setName("invalid");
        List<ModuleInfoDTO> modules = new ArrayList<>();
        modules.add(moduleInfoDTO);
        modulesInfoDTO.setModules(modules);
        appInfoDTO.setModuleInfo(modulesInfoDTO);
        try {
            ResponseDTO responseDTO = confClient.appInfo(cspId, appInfoDTO);
            fail("Expected CategoryCannotBeDeletedException");
        }catch (InvalidModuleNameException e){
            Assert.assertThat(e.getMessage(), containsString(StatusResponseType.API_INVALID_MODULE_NAME.text()));
        }
    }

    /**
     * Test for a CSP that exists with RegistrationIsUpdate=true, with valid module name, invalid module fullname
     */
    @Test
    public void invalidModuleFullNameAppInfoTest() {
        String cspId = "11111111-1111-1111-1111-111111111111";
        AppInfoDTO appInfoDTO = this.appInfoDefaultData;
        ModulesInfoDTO modulesInfoDTO = new ModulesInfoDTO();
        ModuleInfoDTO moduleInfoDTO;


        //set valid module name & invalid module fullname
        moduleInfoDTO = appInfoDTO.getModulesInfo().getModules().get(0);
        moduleInfoDTO.setName("module1");
        ModuleDataDTO moduleDataDTO = new ModuleDataDTO();
        moduleDataDTO.setFullName("invalid");
        moduleInfoDTO.setAdditionalProperties(moduleDataDTO);
        List<ModuleInfoDTO> modules = new ArrayList<>();
        modules.add(moduleInfoDTO);
        modulesInfoDTO.setModules(modules);
        appInfoDTO.setModuleInfo(modulesInfoDTO);
        try {
            ResponseDTO responseDTO = confClient.appInfo(cspId, appInfoDTO);
            fail("Expected CategoryCannotBeDeletedException");
        }catch (InvalidModuleNameException e){
            Assert.assertThat(e.getMessage(), containsString(StatusResponseType.API_INVALID_MODULE_NAME.text()));
        }
    }


    /**
     * Test for a CSP that exists with RegistrationIsUpdate=true, with valid module name, valid module fullname, invalid module version
     */
    @Test
    public void invalidModuleVersionAppInfoTest() {
        String cspId = "11111111-1111-1111-1111-111111111111";
        AppInfoDTO appInfoDTO = this.appInfoDefaultData;
        ModulesInfoDTO modulesInfoDTO = new ModulesInfoDTO();
        ModuleInfoDTO moduleInfoDTO;


        //set valid module name, valid module fullname, invalid module version
        moduleInfoDTO = appInfoDTO.getModulesInfo().getModules().get(0);
        moduleInfoDTO.setName("module1");
        ModuleDataDTO moduleDataDTO = new ModuleDataDTO();
        moduleDataDTO.setFullName("module1.1");
        moduleDataDTO.setVersion(0);
        moduleInfoDTO.setAdditionalProperties(moduleDataDTO);
        List<ModuleInfoDTO> modules = new ArrayList<>();
        modules.add(moduleInfoDTO);
        modulesInfoDTO.setModules(modules);
        appInfoDTO.setModuleInfo(modulesInfoDTO);
        try {
            ResponseDTO responseDTO = confClient.appInfo(cspId, appInfoDTO);
            fail("Expected CategoryCannotBeDeletedException");
        }catch (InvalidModuleVersionException e){
            Assert.assertThat(e.getMessage(), containsString(StatusResponseType.API_INVALID_MODULE_VERSION.text()));
        }
    }

    /**
     * Test for a CSP that exists with RegistrationIsUpdate=true, with valid module name, valid module fullname, valid module version, invalid module hash
     */
    @Test
    public void invalidModuleHashAppInfoTest() {
        String cspId = "11111111-1111-1111-1111-111111111111";
        AppInfoDTO appInfoDTO = this.appInfoDefaultData;
        ModulesInfoDTO modulesInfoDTO = new ModulesInfoDTO();
        ModuleInfoDTO moduleInfoDTO;


        //set valid module name, valid module fullname, valid module version, invalid module hash
        moduleInfoDTO = appInfoDTO.getModulesInfo().getModules().get(0);
        moduleInfoDTO.setName("module1");
        ModuleDataDTO moduleDataDTO = new ModuleDataDTO();
        moduleDataDTO.setFullName("module1.1");
        moduleDataDTO.setVersion(10000);
        moduleDataDTO.setHash("invalid");
        moduleInfoDTO.setAdditionalProperties(moduleDataDTO);
        List<ModuleInfoDTO> modules = new ArrayList<>();
        modules.add(moduleInfoDTO);
        modulesInfoDTO.setModules(modules);
        appInfoDTO.setModuleInfo(modulesInfoDTO);
        try {
            ResponseDTO responseDTO = confClient.appInfo(cspId, appInfoDTO);
            fail("Expected CategoryCannotBeDeletedException");
        }catch (InvalidModuleHashException e){
            Assert.assertThat(e.getMessage(), containsString(StatusResponseType.API_INVALID_MODULE_HASH.text()));
        }
    }

    /**
     * Test for a CSP that exists with with valid module update data
     */
    @Test
    public void validAppInfoTest() {
        String cspId = "11111111-1111-1111-1111-111111111111";
        AppInfoDTO appInfoDTO = this.appInfoDefaultData;
        ModulesInfoDTO modulesInfoDTO = new ModulesInfoDTO();
        ModuleInfoDTO moduleInfoDTO;


        //set valid module name, valid module fullname, valid module version, invalid module hash
        moduleInfoDTO = appInfoDTO.getModulesInfo().getModules().get(0);
        moduleInfoDTO.setName("module1");
        ModuleDataDTO moduleDataDTO = new ModuleDataDTO();
        moduleDataDTO.setFullName("module1.1");
        moduleDataDTO.setVersion(10000);
        moduleDataDTO.setHash("fd61127757973c982cec9d15b61da61a173c8ea86c3122655b92abacec5c7edacff933f896a895d50ecfa3c8f9fc34eb76258bbc228fdf35a5b767a9b1a4c9");
        moduleDataDTO.setInstalledOn("2017-04-24T11:59:35Z");
        moduleDataDTO.setActive(true);
        moduleInfoDTO.setAdditionalProperties(moduleDataDTO);
        List<ModuleInfoDTO> modules = new ArrayList<>();
        modules.add(moduleInfoDTO);
        modulesInfoDTO.setModules(modules);
        appInfoDTO.setModuleInfo(modulesInfoDTO);

        ResponseDTO responseDTO = confClient.appInfo(cspId, appInfoDTO);
        Assert.assertThat(responseDTO.getResponseCode(), equalTo(StatusResponseType.OK.code()));
    }

}
