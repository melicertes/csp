package com.intrasoft.csp.anon.server.repository;

import com.intrasoft.csp.anon.server.model.Mapping;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MappingRepository extends JpaRepository <Mapping, Long>{

    Mapping findTopByDataTypeAndCspId(IntegrationDataType integrationDataType, String cspId);
    Mapping findDistinctByDataTypeAndCspId(IntegrationDataType integrationDataType, String cspId);
}
