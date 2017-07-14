package com.intrasoft.csp.repository;

import com.intrasoft.csp.model.IntegrationAnonData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegrationAnonDataRepository extends JpaRepository <IntegrationAnonData, Long>{
}
