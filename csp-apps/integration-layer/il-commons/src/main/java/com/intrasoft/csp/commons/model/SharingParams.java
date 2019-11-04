package com.intrasoft.csp.commons.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * SharingParams
 */
public class SharingParams implements Serializable {

    private static final long serialVersionUID = -1627144414950921949L;

    @NotNull
    @JsonProperty("toShare")
    private Boolean toShare;

    @NotNull
    @JsonProperty("isExternal")
    private Boolean isExternal;

    @JsonProperty("trustCircleId")
    private List<String> trustCircleIds; //supported values: Array


    @JsonProperty("teamId")
    private List<String> teamIds;//supported values:  Array

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

    public List<String> getTrustCircleIds() {
        return trustCircleIds;
    }

    public void setTrustCircleIds(List<String> trustCircleIds) {
        this.trustCircleIds = trustCircleIds;
    }

    public List<String> getTeamIds() {
        return teamIds;
    }

    public void setTeamIds(List<String> teamIds) {
        this.teamIds = teamIds;
    }

    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        SharingParams that = (SharingParams) object;
        return java.util.Objects.equals(toShare, that.toShare) &&
                java.util.Objects.equals(isExternal, that.isExternal) &&
                java.util.Objects.equals(trustCircleIds, that.trustCircleIds) &&
                java.util.Objects.equals(teamIds, that.teamIds);
    }

    public int hashCode() {
        return Objects.hash(super.hashCode(), toShare, isExternal, trustCircleIds, teamIds);
    }

    @java.lang.Override
    public java.lang.String toString() {
        return new java.util.StringJoiner(", ", SharingParams.class.getSimpleName() + "[", "]")
                .add("toShare=" + toShare)
                .add("isExternal=" + isExternal)
                .add("trustCircleIds=" + trustCircleIds)
                .add("teamIds=" + teamIds)
                .toString();
    }
}

