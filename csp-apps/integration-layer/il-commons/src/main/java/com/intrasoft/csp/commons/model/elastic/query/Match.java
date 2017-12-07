package com.intrasoft.csp.commons.model.elastic.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "dataParams.recordId",
        "dataParams.cspId",
        "dataParams.applicationId",
        "dataParams.originRecordId",
        "dataParams.originCspId",
        "dataParams.originApplicationId"
})
public class Match {

    @JsonProperty("dataParams.recordId")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String recordId;

    @JsonProperty("dataParams.cspId")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cspId;

    @JsonProperty("dataParams.applicationId")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String applicationId;

    @JsonProperty("dataParams.originRecordId")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String originRecordId;

    @JsonProperty("dataParams.originCspId")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String originCspId;

    @JsonProperty("dataParams.originApplicationId")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String originApplicationId;



    @JsonProperty("dataParams.recordId")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getRecordId() {
        return recordId;
    }

    @JsonProperty("dataParams.recordId")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    @JsonProperty("dataParams.cspId")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getCspId() {
        return cspId;
    }

    @JsonProperty("dataParams.cspId")
    public void setCspId(String cspId) {
        this.cspId = cspId;
    }

    @JsonProperty("dataParams.applicationId")
    public String getApplicationId() {
        return applicationId;
    }

    @JsonProperty("dataParams.applicationId")
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }


    @JsonProperty("dataParams.originRecordId")
    public String getOriginRecordId() {
        return originRecordId;
    }

    @JsonProperty("dataParams.originRecordId")
    public void setOriginRecordId(String originRecordId) {
        this.originRecordId = originRecordId;
    }

    @JsonProperty("dataParams.originCspId")
    public String getOriginCspId() {
        return originCspId;
    }

    @JsonProperty("dataParams.originCspId")
    public void setOriginCspId(String originCspId) {
        this.originCspId = originCspId;
    }

    @JsonProperty("dataParams.originApplicationId")
    public String getOriginApplicationId() {
        return originApplicationId;
    }

    @JsonProperty("dataParams.originApplicationId")
    public void setOriginApplicationId(String originApplicationId) {
        this.originApplicationId = originApplicationId;
    }

    @Override
    public String toString() {
        return "Match{" +
                "recordId='" + recordId + '\'' +
                ", cspId='" + cspId + '\'' +
                ", applicationId='" + applicationId + '\'' +
                ", originRecordId='" + originRecordId + '\'' +
                ", originCspId='" + originCspId + '\'' +
                ", originApplicationId='" + originApplicationId + '\'' +
                '}';
    }
}