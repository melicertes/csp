package com.intrasoft.csp.anon.server.model;

import com.intrasoft.csp.anon.commons.model.ApplicationId;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Mapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @NotEmpty
//    @Pattern(regexp = "^[A-Za-z0-9]*$")
    String cspId;

    @NotNull
    @OneToOne
    RuleSet ruleset;

    @NotNull
    IntegrationDataType dataType;

    ApplicationId applicationId;

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

    public RuleSet getRuleset() {
        return ruleset;
    }

    public void setRuleset(RuleSet ruleset) {
        this.ruleset = ruleset;
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
        final StringBuilder sb = new StringBuilder("Mapping{");
        sb.append("id=").append(id);
        sb.append(", cspId='").append(cspId).append('\'');
        sb.append(", ruleset=").append(ruleset);
        sb.append(", dataType=").append(dataType);
        sb.append(", applicationId=").append(applicationId);
        sb.append('}');
        return sb.toString();
    }
}
