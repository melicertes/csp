package com.intrasoft.csp.server.policy.domain.model;

import com.intrasoft.csp.commons.model.IntegrationDataType;

public class PolicyDTO {
    Integer id;
    Boolean active;
    IntegrationDataType integrationDataType;
    String condition;
    SharingPolicyAction sharingPolicyAction;

    public PolicyDTO() {
    }

    public PolicyDTO(Integer id, Boolean active, IntegrationDataType integrationDataType, String condition, SharingPolicyAction sharingPolicyAction) {
        this.id = id;
        this.active = active;
        this.integrationDataType = integrationDataType;
        this.condition = condition;
        this.sharingPolicyAction = sharingPolicyAction;
    }

    public PolicyDTO(Boolean active, IntegrationDataType integrationDataType, String condition, SharingPolicyAction sharingPolicyAction) {
        this.active = active;
        this.integrationDataType = integrationDataType;
        this.condition = condition;
        this.sharingPolicyAction = sharingPolicyAction;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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
