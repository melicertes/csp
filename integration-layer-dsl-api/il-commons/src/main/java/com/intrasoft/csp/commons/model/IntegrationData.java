package com.intrasoft.csp.commons.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * IntegrationData
 */

public class IntegrationData implements Serializable {

    private static final long serialVersionUID = -4036265255253247144L;

    @NotNull
    @Valid
    @JsonProperty("dataParams")
    private DataParams dataParams;

    @NotNull
    @Valid
    @JsonProperty("sharingParams")
    private SharingParams sharingParams;

    @NotNull
    @Valid
    @JsonProperty("dataType")
    private IntegrationDataType dataType;

    @JsonProperty("dataObject")
    private Object dataObject;

    public IntegrationData() {
    }

    public IntegrationData(DataParams dataParams, SharingParams sharingParams, IntegrationDataType dataType, Object dataObject) {
        this.dataParams = dataParams;
        this.sharingParams = sharingParams;
        this.dataType = dataType;
        this.dataObject = dataObject;
    }

    public DataParams getDataParams() {
        return dataParams;
    }

    public void setDataParams(DataParams dataParams) {
        this.dataParams = dataParams;
    }

    public SharingParams getSharingParams() {
        return sharingParams;
    }

    public void setSharingParams(SharingParams sharingParams) {
        this.sharingParams = sharingParams;
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IntegrationData integrationData = (IntegrationData) o;
        return Objects.equals(this.dataParams, integrationData.dataParams) &&
                Objects.equals(this.sharingParams, integrationData.sharingParams) &&
                Objects.equals(this.dataType, integrationData.dataType) &&
                Objects.equals(this.dataObject, integrationData.dataObject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataParams, sharingParams, dataType, dataObject);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class IntegrationData {\n");

        sb.append("    dataParams: ").append(toIndentedString(dataParams)).append("\n");
        sb.append("    sharingParams: ").append(toIndentedString(sharingParams)).append("\n");
        sb.append("    dataType: ").append(toIndentedString(dataType)).append("\n");
        sb.append("    dataObject: ").append(toIndentedString(dataObject)).append("\n");

        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

