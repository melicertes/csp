package com.intrasoft.csp.anon.model;

import com.intrasoft.csp.commons.model.IntegrationDataType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Mapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    String cspId;

    @OneToOne
    Ruleset ruleset;

    IntegrationDataType dataType;

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

    public Ruleset getRuleset() {
        return ruleset;
    }

    public void setRuleset(Ruleset ruleset) {
        this.ruleset = ruleset;
    }

    public IntegrationDataType getDataType() {
        return dataType;
    }

    public void setDataType(IntegrationDataType dataType) {
        this.dataType = dataType;
    }



    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Mapping{");
        sb.append("id=").append(id);
        sb.append(", cspId='").append(cspId).append('\'');
        sb.append(", ruleset=").append(ruleset);
        sb.append(", dataType=").append(dataType);
        sb.append('}');
        return sb.toString();
    }
}
