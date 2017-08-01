package com.intrasoft.csp.anon.repository;

import com.intrasoft.csp.anon.commons.model.IntegrationDataType;
import com.intrasoft.csp.anon.model.Mapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MappingRepository extends JpaRepository <Mapping, Long>{

    public Mapping findTopByDataTypeAndCspId(IntegrationDataType integrationDataType, String cspId);
}
