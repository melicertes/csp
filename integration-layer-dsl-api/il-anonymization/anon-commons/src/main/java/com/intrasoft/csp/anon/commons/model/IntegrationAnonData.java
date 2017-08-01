package com.intrasoft.csp.anon.commons.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.io.Serializable;


/**
 * Created by chris on 26/6/2017.
 */
public class IntegrationAnonData implements Serializable{


    private static final long serialVersionUID = 3100919194708390064L;
    @NotNull
    @JsonProperty("cspId")
    String cspId;

    @NotNull
    @Valid
    @JsonProperty("dataType")
    IntegrationDataType dataType;

    @NotNull
    @Valid
    @JsonProperty("dataObject")
    JsonNode dataObject;

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

    public JsonNode getDataObject() {
        return dataObject;
    }

    public void setDataObject(JsonNode dataObject) {
        this.dataObject = dataObject;
    }

    @Override
    public String toString() {
        return "IntegrationAnonData{" +
                "cspId='" + cspId + '\'' +
                ", dataType=" + dataType +
                ", dataObject=" + dataObject +
                '}';
    }
}
