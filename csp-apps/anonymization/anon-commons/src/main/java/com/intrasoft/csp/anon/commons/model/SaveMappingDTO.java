package com.intrasoft.csp.anon.commons.model;

import com.intrasoft.csp.commons.model.IntegrationDataType;

import java.io.Serializable;

public class SaveMappingDTO implements Serializable{
    private static final long serialVersionUID = -5324987788443097297L;

    private Long id;

    String cspId;

    Long ruleSetId;

    IntegrationDataType dataType;

    ApplicationId applicationId;

    public SaveMappingDTO() {
    }

    public SaveMappingDTO(Long id, String cspId, Long ruleSetId, IntegrationDataType dataType, ApplicationId applicationId) {
        this.id = id;
        this.cspId = cspId;
        this.ruleSetId = ruleSetId;
        this.dataType = dataType;
        this.applicationId = applicationId;
    }

    public SaveMappingDTO(String cspId, Long ruleSetId, IntegrationDataType dataType, ApplicationId applicationId) {
        this.cspId = cspId;
        this.ruleSetId = ruleSetId;
        this.dataType = dataType;
        this.applicationId = applicationId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCspId() {
        return cspId;
    }

    public void setCspId(String cspId) {
        this.cspId = cspId;
    }

    public Long getRuleSetId() {
        return ruleSetId;
    }

    public void setRuleSetId(Long ruleSetId) {
        this.ruleSetId = ruleSetId;
    }

    public IntegrationDataType getDataType() {
        return dataType;
    }

    public void setDataType(IntegrationDataType dataType) {
        this.dataType = dataType;
    }

    public ApplicationId getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(ApplicationId applicationId) {
        this.applicationId = applicationId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SaveMappingDTO{");
        sb.append("id=").append(id);
        sb.append(", cspId='").append(cspId).append('\'');
        sb.append(", ruleSetId=").append(ruleSetId);
        sb.append(", dataType=").append(dataType);
        sb.append(", applicationId=").append(applicationId);
        sb.append('}');
        return sb.toString();
    }
}
