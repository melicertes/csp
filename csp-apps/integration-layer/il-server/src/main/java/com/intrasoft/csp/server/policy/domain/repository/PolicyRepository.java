package com.intrasoft.csp.server.policy.domain.repository;

import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.server.policy.domain.entity.Policy;
import com.intrasoft.csp.server.policy.domain.model.SharingPolicyAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolicyRepository extends JpaRepository<Policy,Integer>{
    List<Policy> findByIntegrationDataType(IntegrationDataType integrationDataType);
    List<Policy> findByIntegrationDataTypeAndActive(IntegrationDataType integrationDataType, Boolean active);
    List<Policy> findBySharingPolicyAction(SharingPolicyAction sharingPolicyAction);
}
