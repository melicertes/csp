package com.intrasoft.csp.anon.commons.interfaces;

import com.intrasoft.csp.anon.commons.exceptions.InvalidDataTypeException;
import com.intrasoft.csp.anon.commons.model.IntegrationAnonData;
import com.intrasoft.csp.anon.commons.model.MappingDTO;
import com.intrasoft.csp.anon.commons.model.RuleSetDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AnonymizationApi {
    /**
     * Anonymize data
     * */
    ResponseEntity<String> postAnonData(IntegrationAnonData integrationAnonData) throws InvalidDataTypeException;

    /**
     * CRUD management for ruleSet
     * */

    /**
     * When {@link RuleSetDTO#id} is provided, it is an update operation,
     * otherwise it is a create operation
     *
     * @return the saved {@link RuleSetDTO}
     * */
    RuleSetDTO saveRuleSet(RuleSetDTO ruleSetDTO);
    void deleteRuleSet(Long id);
    List<RuleSetDTO> getAllRuleSet();
    RuleSetDTO getRuleSetById(Long id);

    /**
     * CRUD management for mappings
     * */

    /**
     * When {@link MappingDTO#id} is provided, it is an update operation,
     * otherwise it is a create operation
     *
     * @return the saved {@link MappingDTO}
     * */
    MappingDTO saveMapping(MappingDTO mappingDTO);
    void deleteMapping(Long id);
    List<MappingDTO> getAllMappings();
    MappingDTO getMappingById(Long id);
}
