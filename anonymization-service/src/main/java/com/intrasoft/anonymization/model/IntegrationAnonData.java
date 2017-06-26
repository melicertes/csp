package com.intrasoft.anonymization.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Created by chris on 26/6/2017.
 */
public class IntegrationAnonData {

    @NotNull
    @JsonProperty("cspId")
    String cspId;

    @NotNull
    @JsonProperty("dataType")
    IntegrationDataType dataType;

    @NotNull
    @JsonProperty("dataObject")
    private Object dataObject;

    public String getCspId() {
        return cspId;
    }

    public void setCspId(String cspId) {
        this.cspId = cspId;
    }

    public IntegrationDataType getDataType() {
        return dataType;
    }

    public void setDataType(IntegrationDataType dataType) {
        this.dataType = dataType;
    }

    public Object getDataObject() {
        return dataObject;
    }

    public void setDataObject(Object dataObject) {
        this.dataObject = dataObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntegrationAnonData that = (IntegrationAnonData) o;

        if (cspId != null ? !cspId.equals(that.cspId) : that.cspId != null) return false;
        if (dataType != that.dataType) return false;
        return dataObject != null ? dataObject.equals(that.dataObject) : that.dataObject == null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cspId, dataType, dataObject);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IntegrationAnonData{");
        sb.append("cspId='").append(cspId).append('\'');
        sb.append(", dataType=").append(dataType);
        sb.append(", dataObject=").append(dataObject);
        sb.append('}');
        return sb.toString();
    }
}
