package com.intrasoft.csp.server.policy.service.impl;

import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.server.policy.domain.model.PolicyDTO;
import com.intrasoft.csp.server.policy.domain.model.SharingPolicyAction;
import com.intrasoft.csp.server.policy.service.SharingPolicyService;

import java.util.List;

public class SharingPolicyImpl implements SharingPolicyService{
    @Override
    public List<PolicyDTO> getPolicies() {
        return null;
    }

    @Override
    public List<PolicyDTO> getPoliciesByAction(SharingPolicyAction sharingPolicyAction) {
        return null;
    }

    @Override
    public List<PolicyDTO> getPoliciesByDataType(IntegrationDataType integrationDataType) {
        return null;
    }

    @Override
    public SharingPolicyAction evaluate(IntegrationDataType integrationDataType) {
        //TODO

        return SharingPolicyAction.NO_ACTION_FOUND;
    }

    @Override
    public PolicyDTO savePolicy(PolicyDTO policyDTO) {
        return null;
    }

    @Override
    public void deletePolicy(Integer id) {

    }

    @Override
    public PolicyDTO getPolicyById(Integer id) {
        return null;
    }
}
