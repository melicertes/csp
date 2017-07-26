package com.intrasoft.csp.commons.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import jdk.nashorn.internal.ir.ObjectNode;

import java.io.Serializable;


/**
 * Created by chris on 26/6/2017.
 */
public class IntegrationAnonData implements Serializable{

    @JsonProperty("cspId")
    String cspId;

    @JsonProperty("dataType")
    IntegrationDataType dataType;

    @JsonProperty("dataObject")
    JsonNode dataObject;


    @JsonProperty("cspId")
    public String getCspId() {
        return cspId;
    }
    @JsonProperty("cspId")
    public void setCspId(String cspId) {
        this.cspId = cspId;
    }

    @JsonProperty("dataType")
    public IntegrationDataType getDataType() {
        return dataType;
    }

    @JsonProperty("dataType")
    public void setDataType(IntegrationDataType dataType) {
        this.dataType = dataType;
    }

    @JsonProperty("dataType")
    public void setDataType(String dataTypeStr) {
        this.dataType = IntegrationDataType.fromValue(dataTypeStr);
    }

    @JsonProperty("dataObject")
    public JsonNode getDataObject() {
        return dataObject;
    }

    @JsonProperty("dataObject")
    public void setDataObject(JsonNode dataObject) {
        this.dataObject = dataObject;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IntegrationAnonData{");
        sb.append("cspId=").append(cspId);
        sb.append(", dataType=").append(dataType);
        sb.append(", dataObject=").append(dataObject);
        sb.append('}');
        return sb.toString();
    }
}
