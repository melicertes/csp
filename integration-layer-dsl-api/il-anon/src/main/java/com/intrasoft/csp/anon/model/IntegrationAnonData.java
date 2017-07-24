package com.intrasoft.csp.anon.model;

import com.fasterxml.jackson.annotation.JsonValue;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Arrays;


/**
 * Created by chris on 26/6/2017.
 */
@Entity
public class IntegrationAnonData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    String cspId;


    byte[] file;

    @OneToOne
    Ruleset ruleset;

    IntegrationDataType dataType;

    IntegrationData integrationData;

    private String configurationFile;

    public IntegrationData getIntegrationData() {
        return integrationData;
    }

    public void setIntegrationData(IntegrationData integrationData) {
        this.integrationData = integrationData;
    }

    public String getCspId() {
        return cspId;
    }

    public void setCspId(String cspId) {
        this.cspId = cspId;
    }

    @JsonValue
    public IntegrationDataType getDataType() {
        return dataType;
    }

    public void setDataType(IntegrationDataType dataType) {
        this.dataType = dataType;
    }

    public void setDataType(String dataTypeStr) {
        this.dataType = IntegrationDataType.fromValue(dataTypeStr);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getConfigurationFile() {
        return configurationFile;
    }

    public void setConfigurationFile(String configurationFile) {
        this.configurationFile = configurationFile;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public Ruleset getRuleset() {
        return ruleset;
    }

    public void setRuleset(Ruleset ruleset) {
        this.ruleset = ruleset;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IntegrationAnonData{");
        sb.append("id=").append(id);
        sb.append(", cspId='").append(cspId).append('\'');
        sb.append(", file=").append(Arrays.toString(file));
        sb.append(", ruleset=").append(ruleset);
        sb.append(", dataType=").append(dataType);
        sb.append(", integrationData=").append(integrationData);
        sb.append(", configurationFile='").append(configurationFile).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
