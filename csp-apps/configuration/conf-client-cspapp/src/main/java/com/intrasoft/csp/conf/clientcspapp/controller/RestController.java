package com.intrasoft.csp.conf.clientcspapp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.conf.clientcspapp.context.ContextUrl;
import com.intrasoft.csp.conf.commons.context.ApiContextUrl;
import com.intrasoft.csp.conf.commons.model.api.ModulesInfoDTO;
import com.intrasoft.csp.conf.commons.model.api.RegistrationDTO;
import com.intrasoft.csp.conf.commons.model.api.ResponseDTO;
import com.intrasoft.csp.conf.commons.model.forms.CspForm;
import com.intrasoft.csp.conf.commons.utils.JodaConverter;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.libraries.versioning.client.ApiVersionClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@org.springframework.web.bind.annotation.RestController
public class RestController implements ContextUrl, ApiContextUrl {

    @Autowired
    @Qualifier("ConfApiVersionClient")
    ApiVersionClient apiVersionClient;

    @Autowired
    @Qualifier("ConfRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @RequestMapping(value = REST_REGISTER + "/{cspId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            method = RequestMethod.POST)
    public ResponseDTO register(@PathVariable String cspId, @RequestBody CspForm cspForm) {
        final String url = apiVersionClient.getApiUrl() + API_REGISTER + "/" + cspId;

        RegistrationDTO cspRegistration = new RegistrationDTO();
        cspRegistration.setName(cspForm.getName());
        cspRegistration.setDomainName(cspForm.getDomainName());
        cspRegistration.setRegistrationDate(JodaConverter.getCurrentJodaString());
        cspRegistration.setExternalIPs(cspForm.getExternalIps());
        cspRegistration.setInternalIPs(cspForm.getInternalIps());
        cspRegistration.setRegistrationIsUpdate(false);
        cspRegistration.setContacts(cspForm.getContactDetails());
        ModulesInfoDTO modulesInfo = new ModulesInfoDTO();
        cspRegistration.setModuleInfo(modulesInfo);
        System.out.println(cspRegistration.toString());
        ResponseDTO responseDTO = retryRestTemplate.postForObject(url, cspRegistration, ResponseDTO.class);
        return responseDTO;
    }

    /**
     * TEST
     */
    @RequestMapping(value = REST_LOG,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            method = RequestMethod.GET)
    public ResponseEntity log() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Object>> mapType = new TypeReference<List<Object>>() {};

        File initialFile = new File("e://0//generated.json");


        //InputStream is = TypeReference.class.getResourceAsStream("/generated.json");
        List<Object> exampleList = new ArrayList<>();
        try {
            InputStream is = new FileInputStream(initialFile);
            exampleList = mapper.readValue(is, mapType);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return new ResponseEntity<>(exampleList, HttpStatus.OK);
    }
}
