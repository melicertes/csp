package com.intrasoft.csp.anon.server.service.impl;

import com.intrasoft.csp.anon.commons.exceptions.InvalidDataTypeException;
import com.intrasoft.csp.anon.commons.model.IntegrationAnonData;
import com.intrasoft.csp.anon.commons.model.MappingDTO;
import com.intrasoft.csp.anon.commons.model.RuleSetDTO;
import com.intrasoft.csp.anon.commons.model.SaveMappingDTO;
import com.intrasoft.csp.anon.server.model.Mapping;
import com.intrasoft.csp.anon.server.model.RuleSet;
import com.intrasoft.csp.anon.server.repository.MappingRepository;
import com.intrasoft.csp.anon.server.repository.RuleSetRepository;
import com.intrasoft.csp.anon.server.service.AnonService;
import com.intrasoft.csp.anon.server.utils.Conversions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnonServiceImpl implements AnonService,Conversions {
    private static final Logger LOG = LoggerFactory.getLogger(AnonServiceImpl.class);

    @Autowired
    RuleSetRepository rulesetRepository ;

    @Autowired
    MappingRepository mappingRepository;

    @Override
    public ResponseEntity<String> postAnonData(IntegrationAnonData integrationAnonData) throws InvalidDataTypeException {
        return null;
    }

    @Override
    public RuleSetDTO saveRuleSet(RuleSetDTO ruleSetDTO) {
        RuleSet ruleSet = null;
        if(ruleSetDTO.getId() == null){//insert
            ruleSet = new RuleSet();
        }else{//update
            ruleSet = rulesetRepository.findOne(ruleSetDTO.getId());
        }

        if(!StringUtils.isEmpty(ruleSetDTO.getDescription())) {
            ruleSet.setDescription(ruleSetDTO.getDescription());
        }
        if(ruleSetDTO.getFile()!=null) {
            ruleSet.setFile(ruleSetDTO.getFile());
        }
        if(!StringUtils.isEmpty(ruleSetDTO.getFilename())) {
            ruleSet.setFilename(ruleSetDTO.getFilename());
        }

        RuleSet saved = rulesetRepository.save(ruleSet);
        return convertRuleSetToDTO.apply(saved);
    }

    @Override
    public void deleteRuleSet(Long id) {
        rulesetRepository.delete(id);
    }

    @Override
    public List<RuleSetDTO> getAllRuleSet() {
        return rulesetRepository.findAll().stream().map(r->{
            RuleSetDTO ruleSetDTO = convertRuleSetToDTO.apply(r);
            return ruleSetDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public RuleSetDTO getRuleSetById(Long id) {
        return convertRuleSetToDTO.apply(rulesetRepository.findOne(id));
    }

    @Override
    public MappingDTO saveMapping(MappingDTO mappingDTO) {
        Mapping mapping = null;
        if(mappingDTO.getId() == null){//insert
            mapping = new Mapping();
        }else{//update
            mapping = mappingRepository.findOne(mappingDTO.getId());
        }

        if(!StringUtils.isEmpty(mappingDTO.getCspId())) {
            mapping.setCspId(mappingDTO.getCspId());
        }
        if(mappingDTO.getRuleSetDTO()!=null) {
            mapping.setRuleset(rulesetRepository.findOne(mappingDTO.getRuleSetDTO().getId()));
        }
        if(mappingDTO.getDataType()!=null) {
            mapping.setDataType(mappingDTO.getDataType());
        }

        Mapping saved = mappingRepository.save(mapping);
        return convertMappingToDTO.apply(saved);
    }

    @Override
    public MappingDTO saveMapping(SaveMappingDTO saveMappingDTO) {
        Mapping mapping = null;
        if(saveMappingDTO.getId() == null){//insert
            mapping = new Mapping();
        }else{//update
            mapping = mappingRepository.findOne(saveMappingDTO.getId());
        }

        if(!StringUtils.isEmpty(saveMappingDTO.getCspId())) {
            mapping.setCspId(saveMappingDTO.getCspId());
        }
        if(saveMappingDTO.getRuleSetId()!=null) {
            mapping.setRuleset(rulesetRepository.findOne(saveMappingDTO.getRuleSetId()));
        }
        if(saveMappingDTO.getDataType()!=null) {
            mapping.setDataType(saveMappingDTO.getDataType());
        }

        Mapping saved = mappingRepository.save(mapping);
        return convertMappingToDTO.apply(saved);
    }

    @Override
    public void deleteMapping(Long id) {
        mappingRepository.delete(id);
    }

    @Override
    public MappingDTO getMappingById(Long id) {
        return convertMappingToDTO.apply(mappingRepository.findOne(id));
    }

    @Override
    public List<MappingDTO> getAllMappings() {
        return mappingRepository.findAll().stream().map(m->{
            MappingDTO mappingDTO = convertMappingToDTO.apply(m);
            return mappingDTO;
        }).collect(Collectors.toList());
    }
}
