package com.intrasoft.csp.anon.commons.model;

import com.intrasoft.csp.commons.model.IntegrationDataType;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class MappingDTO implements Serializable{
    private static final long serialVersionUID = -3938233520434048789L;

    private Long id;

    String cspId;

    RuleSetDTO ruleSetDTO;

    ApplicationId applicationId;

    IntegrationDataType dataType;

    public MappingDTO() {
    }

    /**
     * Constructor to update a new RuleSet
     * */
    public MappingDTO(Long id, String cspId, RuleSetDTO ruleSetDTO, IntegrationDataType dataType, ApplicationId applicationId) {
        this.id = id;
        this.cspId = cspId;
        this.ruleSetDTO = ruleSetDTO;
        this.dataType = dataType;
        this.applicationId = applicationId;
    }

    /**
     * Constructor to create a new RuleSet
     * */
    public MappingDTO(String cspId, RuleSetDTO ruleSetDTO, IntegrationDataType dataType, ApplicationId applicationId) {
        this.cspId = cspId;
        this.ruleSetDTO = ruleSetDTO;
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

    public RuleSetDTO getRuleSetDTO() {
        return ruleSetDTO;
    }

    public void setRuleSetDTO(RuleSetDTO ruleSetDTO) {
        this.ruleSetDTO = ruleSetDTO;
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
        final StringBuilder sb = new StringBuilder("MappingDTO{");
        sb.append("id=").append(id);
        sb.append(", cspId='").append(cspId).append('\'');
        sb.append(", ruleSetDTO=").append(ruleSetDTO);
        sb.append(", applicationId=").append(applicationId);
        sb.append(", dataType=").append(dataType);
        sb.append('}');
        return sb.toString();
    }

}
