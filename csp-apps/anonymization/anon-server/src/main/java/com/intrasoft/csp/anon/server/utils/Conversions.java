package com.intrasoft.csp.anon.server.utils;

import com.intrasoft.csp.anon.commons.model.MappingDTO;
import com.intrasoft.csp.anon.commons.model.RuleSetDTO;
import com.intrasoft.csp.anon.server.model.Mapping;
import com.intrasoft.csp.anon.server.model.RuleSet;

import java.util.function.Function;

public interface Conversions {
    Function<RuleSet, RuleSetDTO> convertRuleSetToDTO = (ruleSet) -> {
        final RuleSetDTO ruleSetDTO = new RuleSetDTO();
        ruleSetDTO.setId(ruleSet.getId());
        ruleSetDTO.setFile(ruleSet.getFile());
        ruleSetDTO.setDescription(ruleSet.getDescription());
        ruleSetDTO.setFilename(ruleSet.getFilename());
        return ruleSetDTO;
    };

    Function<Mapping, MappingDTO> convertMappingToDTO = (mapping) -> {
        final MappingDTO mappingDTO = new MappingDTO();
        mappingDTO.setCspId(mapping.getCspId());
        mappingDTO.setDataType(mapping.getDataType());
        mappingDTO.setId(mapping.getId());
        mappingDTO.setRuleSetDTO(convertRuleSetToDTO.apply(mapping.getRuleset()));
        mappingDTO.setCspId(mapping.getCspId());
        mappingDTO.setApplicationId(mapping.getApplicationId());

        return mappingDTO;
    };
}
