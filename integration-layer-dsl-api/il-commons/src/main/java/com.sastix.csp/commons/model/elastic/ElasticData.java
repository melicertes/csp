package com.sastix.csp.commons.model.elastic;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.sastix.csp.commons.model.DataParams;

import java.io.Serializable;
import java.util.Objects;

/**
 * IntegrationData
 */
public class ElasticData  implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("dataParams")
    private DataParams dataParams;

    @JsonProperty("dataObject")
    private Object dataObject;

    public ElasticData() {}

    public ElasticData(DataParams dataParams, Object dataObject) {
        this.dataParams = dataParams;
        this.dataObject = dataObject;
    }

    public DataParams getDataParams() {
        return dataParams;
    }

    public void setDataParams(DataParams dataParams) {
        this.dataParams = dataParams;
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
        ElasticData elasticData = (ElasticData) o;
        return Objects.equals(this.dataParams, elasticData.dataParams) &&
                Objects.equals(this.dataObject, elasticData.dataObject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataParams, dataObject);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class IntegrationData {\n");

        sb.append("    dataParams: ").append(toIndentedString(dataParams)).append("\n");
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

