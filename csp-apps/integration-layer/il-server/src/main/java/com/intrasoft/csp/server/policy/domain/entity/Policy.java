package com.intrasoft.csp.server.policy.domain.entity;

import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.elastic.query.Bool;
import com.intrasoft.csp.server.policy.domain.model.SharingPolicyAction;

import javax.persistence.*;

@Entity
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Enumerated(EnumType.STRING)
    IntegrationDataType integrationDataType;

    @Column
    Boolean active;

    @Column(columnDefinition = "Text")
    String policyCondition;

    @Enumerated(EnumType.STRING)
    SharingPolicyAction sharingPolicyAction;

    public Policy() {
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

    public String getPolicyCondition() {
        return policyCondition;
    }

    public void setPolicyCondition(String policyCondition) {
        this.policyCondition = policyCondition;
    }

    public SharingPolicyAction getSharingPolicyAction() {
        return sharingPolicyAction;
    }

    public void setSharingPolicyAction(SharingPolicyAction sharingPolicyAction) {
        this.sharingPolicyAction = sharingPolicyAction;
    }
}
