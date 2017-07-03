package com.intrasoft.anonymization.repository;

import com.intrasoft.anonymization.model.IntegrationAnonData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegrationAnonDataRepository extends JpaRepository <IntegrationAnonData, Long>{
}
