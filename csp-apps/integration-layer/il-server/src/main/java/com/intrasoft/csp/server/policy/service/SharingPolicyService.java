package com.intrasoft.csp.server.policy.service;

import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.Team;
import com.intrasoft.csp.server.policy.domain.exception.PolicyNotFoundException;
import com.intrasoft.csp.server.policy.domain.exception.PolicySaveException;
import com.intrasoft.csp.server.policy.domain.model.EvaluatedPolicyDTO;
import com.intrasoft.csp.server.policy.domain.model.PolicyDTO;
import com.intrasoft.csp.server.policy.domain.model.SharingPolicyAction;

import javax.script.ScriptException;
import java.util.List;

public interface SharingPolicyService {
    List<PolicyDTO> getPolicies();
    List<PolicyDTO> getPoliciesByAction(SharingPolicyAction sharingPolicyAction);
    List<PolicyDTO> getPoliciesByDataType(IntegrationDataType integrationDataType);
    EvaluatedPolicyDTO evaluate(IntegrationDataType integrationDataType);
    PolicyDTO savePolicy(PolicyDTO policyDTO) throws PolicySaveException;
    void deletePolicy(Integer id);
    void deleteAllPolicies();
    PolicyDTO getPolicyById(Integer id) throws PolicyNotFoundException;
    Boolean checkCondition(String condition, IntegrationData integrationData, Team team) throws ScriptException;
}
