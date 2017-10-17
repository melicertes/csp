package com.intrasoft.csp.libraries.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * DataParams
 */
public class DataParams implements Serializable {

    private static final long serialVersionUID = -9120520939968768922L;

    @NotNull
    @JsonProperty("cspId")
    private String cspId;

    @NotNull
    @JsonProperty("applicationId")
    private String applicationId;

    @NotNull
    @JsonProperty("recordId")
    private String recordId;

    @NotNull
    @JsonProperty("dateTime")
    private DateTime dateTime;

    @NotNull
    @JsonProperty("originCspId")
    private String originCspId;

    @NotNull
    @JsonProperty("originApplicationId")
    private String originApplicationId;

    @NotNull
    @JsonProperty("originRecordId")
    private String originRecordId;

    @JsonProperty("url")
    private String url;

    public DataParams() {
    }

    public DataParams(String cspId, String applicationId, String recordId) {
        this.cspId = cspId;
        this.applicationId = applicationId;
        this.recordId = recordId;
    }

    public DataParams(String cspId, String applicationId, String recordId, DateTime dateTime, String originCspId, String originApplicationId, String originRecordId) {
        this.cspId = cspId;
        this.applicationId = applicationId;
        this.recordId = recordId;
        this.dateTime = dateTime;
        this.originCspId = originCspId;
        this.originApplicationId = originApplicationId;
        this.originRecordId = originRecordId;
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

    public String getOriginCspId() {
        return originCspId;
    }

    public void setOriginCspId(String originCspId) {
        this.originCspId = originCspId;
    }

    public String getOriginApplicationId() {
        return originApplicationId;
    }

    public void setOriginApplicationId(String originApplicationId) {
        this.originApplicationId = originApplicationId;
    }

    public String getOriginRecordId() {
        return originRecordId;
    }

    public void setOriginRecordId(String originRecordId) {
        this.originRecordId = originRecordId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
        return "DataParams{" +
                "cspId='" + cspId + '\'' +
                ", applicationId='" + applicationId + '\'' +
                ", recordId='" + recordId + '\'' +
                ", dateTime=" + dateTime +
                ", originCspId='" + originCspId + '\'' +
                ", originApplicationId='" + originApplicationId + '\'' +
                ", originRecordId='" + originRecordId + '\'' +
                ", url='" + url + '\'' +
                '}';
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

