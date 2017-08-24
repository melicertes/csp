package com.intrasoft.csp.server.policy.domain.model;

public class EvaluatedPolicyDTO {
    SharingPolicyAction sharingPolicyAction;
    PolicyDTO policyDTO;

    public EvaluatedPolicyDTO() {
    }

    public EvaluatedPolicyDTO(SharingPolicyAction sharingPolicyAction, PolicyDTO policyDTO) {
        this.sharingPolicyAction = sharingPolicyAction;
        this.policyDTO = policyDTO;
    }

    public SharingPolicyAction getSharingPolicyAction() {
        return sharingPolicyAction;
    }

    public void setSharingPolicyAction(SharingPolicyAction sharingPolicyAction) {
        this.sharingPolicyAction = sharingPolicyAction;
    }

    public PolicyDTO getPolicyDTO() {
        return policyDTO;
    }

    public void setPolicyDTO(PolicyDTO policyDTO) {
        this.policyDTO = policyDTO;
    }
}
