package com.intrasoft.csp.server.utils;

import com.intrasoft.csp.server.policy.domain.entity.Policy;
import com.intrasoft.csp.server.policy.domain.model.PolicyDTO;

import java.util.function.Function;

public interface Conversions {
    Function<Policy, PolicyDTO> convertPolicyToDTO = (p) -> {
        final PolicyDTO dto = new PolicyDTO();
        dto.setId(p.getId());
        dto.setActive(p.getActive());
        dto.setIntegrationDataType(p.getIntegrationDataType());
        dto.setCondition(p.getPolicyCondition());
        dto.setSharingPolicyAction(p.getSharingPolicyAction());
        return dto;
    };
}
