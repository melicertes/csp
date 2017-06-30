package com.intrasoft.anonymization.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Created by chris on 26/6/2017.
 */
@Entity
public class IntegrationAnonData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @JsonProperty("cspId")
    String cspId;


    @JsonProperty("dataType")
    IntegrationDataType dataType;

    private String configurationFile;

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IntegrationAnonData{");
        sb.append("cspId='").append(cspId).append('\'');
        sb.append(", dataType=").append(dataType);
        sb.append(", configurationFilePath='").append(configurationFile).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
