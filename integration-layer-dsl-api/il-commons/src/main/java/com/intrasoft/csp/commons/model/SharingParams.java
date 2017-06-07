package com.intrasoft.csp.commons.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

/**
 * SharingParams
 */
public class SharingParams implements Serializable {

    private static final long serialVersionUID = -1627144414950921949L;

    @JsonProperty("toShare")
    private Boolean toShare;

    @JsonProperty("isExternal")
    private Boolean isExternal;

    public SharingParams() {
    }

    public SharingParams(Boolean toShare, Boolean isExternal) {
        this.toShare = toShare;
        this.isExternal = isExternal;
    }

    public Boolean getToShare() {
        return toShare;
    }

    public void setToShare(Boolean toShare) {
        this.toShare = toShare;
    }

    public Boolean getIsExternal() {
        return isExternal;
    }

    public void setIsExternal(Boolean isExternal) {
        this.isExternal = isExternal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SharingParams sharingParams = (SharingParams) o;
        return Objects.equals(this.toShare, sharingParams.toShare) &&
                Objects.equals(this.isExternal, sharingParams.isExternal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toShare, isExternal);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SharingParams {\n");

        sb.append("    toShare: ").append(toIndentedString(toShare)).append("\n");
        sb.append("    isExternal: ").append(toIndentedString(isExternal)).append("\n");
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

