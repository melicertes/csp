package com.intrasoft.csp.repository;

import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.model.IntegrationAnonData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntegrationAnonDataRepository extends JpaRepository <IntegrationAnonData, Long>{

    public List<IntegrationAnonData> findDistinctByDataType(IntegrationDataType integrationDataType);
}
