package com.intrasoft.csp.anon.server.controller;
import com.intrasoft.csp.anon.commons.model.AnonContextUrl;
import com.intrasoft.csp.anon.commons.model.IntegrationAnonData;
import com.intrasoft.csp.anon.commons.model.MappingDTO;
import com.intrasoft.csp.anon.commons.model.RuleSetDTO;
import com.intrasoft.csp.anon.server.service.AnonService;
import com.intrasoft.csp.anon.server.service.ApiDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;


@RestController
public class AnonController implements AnonContextUrl {

    private static final Logger LOG = LoggerFactory.getLogger(AnonController.class);

    @Autowired
    ApiDataHandler apiDataHandler;

    @Autowired
    AnonService anonService;

    @RequestMapping(value = "/v"+REST_API_V1+"/"+ANONYMIZE, method = RequestMethod.POST)
    public IntegrationAnonData anonData(@RequestBody IntegrationAnonData integrationAnonData) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
        LOG.info("Anon Endpoint: POST received");
        return anonService.postAnonData(integrationAnonData);
    }

    @RequestMapping(value = "/v"+REST_API_V1+"/"+SAVE_RULESET, method = RequestMethod.POST)
    public RuleSetDTO saveRuleSet(@RequestBody RuleSetDTO ruleSetDTO){
        return anonService.saveRuleSet(ruleSetDTO);
    }

    @RequestMapping(value = "/v"+REST_API_V1+"/"+DELETE_RULESET, method = RequestMethod.POST)
    public void deleteRuleSet(@RequestBody Long id){
        anonService.deleteRuleSet(id);
    }

    @RequestMapping(value = "/v"+REST_API_V1+"/"+GET_ALL_RULESET, method = RequestMethod.GET)
    public List<RuleSetDTO> getAllRuleSet(){
        return anonService.getAllRuleSet();
    }


    @RequestMapping(value = "/v"+REST_API_V1+"/"+SAVE_MAPPING, method = RequestMethod.POST)
    public MappingDTO saveMapping(@RequestBody MappingDTO mappingDTO){
        return anonService.saveMapping(mappingDTO);
    }

    @RequestMapping(value = "/v"+REST_API_V1+"/"+DELETE_MAPPING, method = RequestMethod.POST)
    public void deleteMapping(@RequestBody Long id){
        anonService.deleteMapping(id);
    }

    @RequestMapping(value = "/v"+REST_API_V1+"/"+GET_MAPPING+"/{id}", method = RequestMethod.GET)
    public MappingDTO getMapping(@PathVariable Long id){
        return anonService.getMappingById(id);
    }

    @RequestMapping(value = "/v"+REST_API_V1+"/"+GET_ALL_MAPPINGS, method = RequestMethod.GET)
    public List<MappingDTO> getAllMappings(){
        return anonService.getAllMappings();
    }

}
