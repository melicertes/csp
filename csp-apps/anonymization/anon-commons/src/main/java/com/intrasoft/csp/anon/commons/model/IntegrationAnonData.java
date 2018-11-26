package com.intrasoft.csp.anon.commons.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.intrasoft.csp.commons.model.DataParams;
import com.intrasoft.csp.commons.model.IntegrationDataType;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Map;


/**
 * Created by chris on 26/6/2017.
 */
public class IntegrationAnonData implements Serializable{

    private static final long serialVersionUID = 3100919194708390064L;

    @NotNull
    String cspId;

    @NotNull
    String applicationId;

    @NotNull
    @Valid
    @JsonProperty("dataType")
    IntegrationDataType dataType;

    @NotNull
    @Valid
    @JsonProperty("dataObject")
    Object dataObject;

    @JsonProperty("dataParams")
    private void unpackFromDataParams(Map<String, String> dataParams) {
        cspId = dataParams.get("cspId");
        applicationId = dataParams.get("applicationId");
    }

    public IntegrationAnonData() {
    }

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

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IntegrationAnonData{");
        sb.append("cspId='").append(cspId).append('\'');
        sb.append(", dataType=").append(dataType);
        sb.append(", applicationId='").append(applicationId).append('\'');
        sb.append(", dataObject=").append(dataObject);
        sb.append('}');
        return sb.toString();
    }
}
