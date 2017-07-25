package com.intrasoft.csp.commons.model;

import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Created by chris on 26/6/2017.
 */
public class IntegrationAnonData {

    String cspId;

    IntegrationDataType dataType;

    Object dataObject;



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

    public Object getDataObject() {
        return dataObject;
    }

    public void setDataObject(Object dataObject) {
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
