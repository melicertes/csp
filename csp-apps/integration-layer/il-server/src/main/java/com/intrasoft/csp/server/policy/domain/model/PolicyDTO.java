package com.intrasoft.csp.server.policy.domain.model;

import com.intrasoft.csp.commons.model.IntegrationDataType;

public class PolicyDTO {
    Integer id;
    IntegrationDataType integrationDataType;
    String condition;
    SharingPolicyAction sharingPolicyAction;

    public PolicyDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public IntegrationDataType getIntegrationDataType() {
        return integrationDataType;
    }

    public void setIntegrationDataType(IntegrationDataType integrationDataType) {
        this.integrationDataType = integrationDataType;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public SharingPolicyAction getSharingPolicyAction() {
        return sharingPolicyAction;
    }

    public void setSharingPolicyAction(SharingPolicyAction sharingPolicyAction) {
        this.sharingPolicyAction = sharingPolicyAction;
    }
}
