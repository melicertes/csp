package com.intrasoft.csp.anon.commons.model;

import com.intrasoft.csp.commons.model.IntegrationDataType;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class MappingDTO implements Serializable{
    private static final long serialVersionUID = -3938233520434048789L;

    private long id;

    String cspId;

    RuleSetDTO ruleSetDTO;

    IntegrationDataType dataType;

    public MappingDTO() {
    }

    /**
     * Constructor to update a new RuleSet
     * */
    public MappingDTO(long id, String cspId, RuleSetDTO ruleSetDTO, IntegrationDataType dataType) {
        this.id = id;
        this.cspId = cspId;
        this.ruleSetDTO = ruleSetDTO;
        this.dataType = dataType;
    }

    /**
     * Constructor to create a new RuleSet
     * */
    public MappingDTO(String cspId, RuleSetDTO ruleSetDTO, IntegrationDataType dataType) {
        this.cspId = cspId;
        this.ruleSetDTO = ruleSetDTO;
        this.dataType = dataType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    @Override
    public String toString() {
        return "MappingDTO{" +
                "id=" + id +
                ", cspId='" + cspId + '\'' +
                ", ruleSetDTO=" + ruleSetDTO +
                ", dataType=" + dataType +
                '}';
    }
}
