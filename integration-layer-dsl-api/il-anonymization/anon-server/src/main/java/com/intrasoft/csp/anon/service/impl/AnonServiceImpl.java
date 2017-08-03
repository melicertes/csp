package com.intrasoft.csp.anon.service.impl;

import com.intrasoft.csp.anon.commons.exceptions.InvalidDataTypeException;
import com.intrasoft.csp.anon.commons.model.IntegrationAnonData;
import com.intrasoft.csp.anon.commons.model.MappingDTO;
import com.intrasoft.csp.anon.commons.model.RuleSetDTO;
import com.intrasoft.csp.anon.service.AnonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnonServiceImpl implements AnonService {
    private static final Logger LOG = LoggerFactory.getLogger(AnonServiceImpl.class);
    @Override
    public ResponseEntity<String> postAnonData(IntegrationAnonData integrationAnonData) throws InvalidDataTypeException {
        return null;
    }

    @Override
    public RuleSetDTO saveRuleSet(RuleSetDTO ruleSetDTO) {
        return null;
    }

    @Override
    public void deleteRuleSet(Long id) {

    }

    @Override
    public List<RuleSetDTO> getAllRuleSet() {
        return null;
    }

    @Override
    public MappingDTO saveMapping(MappingDTO mappingDTO) {
        return null;
    }

    @Override
    public void deleteMapping(Long id) {

    }

    @Override
    public MappingDTO getMappingById(Long id) {
        return null;
    }
}
