package com.intrasoft.csp.anon.commons.model;

import com.intrasoft.csp.commons.model.IntegrationDataType;

import java.io.Serializable;

public class SaveMappingDTO implements Serializable{
    private static final long serialVersionUID = -5324987788443097297L;

    private Long id;

    String cspId;

    Long ruleSetId;

    IntegrationDataType dataType;

    public SaveMappingDTO() {
    }

    public SaveMappingDTO(Long id, String cspId, Long ruleSetId, IntegrationDataType dataType) {
        this.id = id;
        this.cspId = cspId;
        this.ruleSetId = ruleSetId;
        this.dataType = dataType;
    }

    public SaveMappingDTO(String cspId, Long ruleSetId, IntegrationDataType dataType) {
        this.cspId = cspId;
        this.ruleSetId = ruleSetId;
        this.dataType = dataType;
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

    @Override
    public String toString() {
        return "SaveMappingDTO{" +
                "id=" + id +
                ", cspId='" + cspId + '\'' +
                ", ruleSetId=" + ruleSetId +
                ", dataType=" + dataType +
                '}';
    }
}
