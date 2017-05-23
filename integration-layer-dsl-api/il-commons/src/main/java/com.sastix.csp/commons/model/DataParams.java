package com.sastix.csp.commons.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Objects;

/**
 * DataParams
 */
public class DataParams implements Serializable {

    private static final long serialVersionUID = -9120520939968768922L;

    @JsonProperty("cspId")
    private String cspId;

    @JsonProperty("applicationId")
    private String applicationId;

    @JsonProperty("recordId")
    private String recordId;

    @JsonProperty("dateTime")
    private DateTime dateTime;

    public DataParams() {
    }

    public DataParams(String cspId, String applicationId, String recordId) {
        this.cspId = cspId;
        this.applicationId = applicationId;
        this.recordId = recordId;
    }

    public DataParams(String cspId, String applicationId, String recordId, DateTime dateTime) {
        this.cspId = cspId;
        this.applicationId = applicationId;
        this.recordId = recordId;
        this.dateTime = dateTime;
    }

    public String getCspId() {
        return cspId;
    }

    public void setCspId(String cspId) {
        this.cspId = cspId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataParams dataParams = (DataParams) o;
        return Objects.equals(this.cspId, dataParams.cspId) &&
                Objects.equals(this.applicationId, dataParams.applicationId) &&
                Objects.equals(this.recordId, dataParams.recordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cspId, applicationId, recordId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DataParams {\n");

        sb.append("    cspId: ").append(toIndentedString(cspId)).append("\n");
        sb.append("    applicationId: ").append(toIndentedString(applicationId)).append("\n");
        sb.append("    recordId: ").append(toIndentedString(recordId)).append("\n");
        sb.append("    dateTime: ").append(toIndentedString(dateTime)).append("\n");
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

