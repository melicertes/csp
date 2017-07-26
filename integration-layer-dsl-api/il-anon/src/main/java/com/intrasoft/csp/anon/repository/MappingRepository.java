package com.intrasoft.csp.anon.repository;

import com.intrasoft.csp.anon.model.Mapping;
import com.intrasoft.csp.commons.model.IntegrationDataType;
//import com.intrasoft.csp.anon.model.IntegrationAnonData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MappingRepository extends JpaRepository <Mapping, Long>{

    public List<Mapping> findDistinctByDataType(IntegrationDataType integrationDataType);
}
