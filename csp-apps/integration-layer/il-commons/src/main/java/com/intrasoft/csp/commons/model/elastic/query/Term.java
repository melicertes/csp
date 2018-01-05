package com.intrasoft.csp.commons.model.elastic.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "dataParams.recordId.keyword",
        "dataParams.cspId.keyword",
        "dataParams.applicationId.keyword",
        "dataParams.originRecordId.keyword",
        "dataParams.originCspId.keyword",
        "dataParams.originApplicationId.keyword"
})
public class Term {

    @JsonProperty("dataParams.recordId.keyword")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String recordId;

    @JsonProperty("dataParams.cspId.keyword")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cspId;

    @JsonProperty("dataParams.applicationId.keyword")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String applicationId;

    @JsonProperty("dataParams.originRecordId.keyword")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String originRecordId;

    @JsonProperty("dataParams.originCspId.keyword")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String originCspId;

    @JsonProperty("dataParams.originApplicationId.keyword")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String originApplicationId;



    @JsonProperty("dataParams.recordId.keyword")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getRecordId() {
        return recordId;
    }

    @JsonProperty("dataParams.recordId.keyword")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    @JsonProperty("dataParams.cspId.keyword")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getCspId() {
        return cspId;
    }

    @JsonProperty("dataParams.cspId.keyword")
    public void setCspId(String cspId) {
        this.cspId = cspId;
    }

    @JsonProperty("dataParams.applicationId.keyword")
    public String getApplicationId() {
        return applicationId;
    }

    @JsonProperty("dataParams.applicationId.keyword")
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }


    @JsonProperty("dataParams.originRecordId.keyword")
    public String getOriginRecordId() {
        return originRecordId;
    }

    @JsonProperty("dataParams.originRecordId.keyword")
    public void setOriginRecordId(String originRecordId) {
        this.originRecordId = originRecordId;
    }

    @JsonProperty("dataParams.originCspId.keyword")
    public String getOriginCspId() {
        return originCspId;
    }

    @JsonProperty("dataParams.originCspId.keyword")
    public void setOriginCspId(String originCspId) {
        this.originCspId = originCspId;
    }

    @JsonProperty("dataParams.originApplicationId.keyword")
    public String getOriginApplicationId() {
        return originApplicationId;
    }

    @JsonProperty("dataParams.originApplicationId.keyword")
    public void setOriginApplicationId(String originApplicationId) {
        this.originApplicationId = originApplicationId;
    }

    @Override
    public String toString() {
        return "Term{" +
                "recordId='" + recordId + '\'' +
                ", cspId='" + cspId + '\'' +
                ", applicationId='" + applicationId + '\'' +
                ", originRecordId='" + originRecordId + '\'' +
                ", originCspId='" + originCspId + '\'' +
                ", originApplicationId='" + originApplicationId + '\'' +
                '}';
    }
}