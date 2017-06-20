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
        "dataParams.applicationId"
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

    @Override
    public String toString() {
        return "Term{" +
                "recordId='" + recordId + '\'' +
                ", cspId='" + cspId + '\'' +
                ", applicationId='" + applicationId + '\'' +
                '}';
    }
}