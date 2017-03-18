package com.sastix.csp.commons.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

/**
 * SharingParams
 */
public class SharingParams  implements Serializable {

  private static final long serialVersionUID = 1L;

  @JsonProperty("toShare")
  private Boolean toShare;

  @JsonProperty("trustCircleId")
  private String trustCircleId;

    @JsonProperty("external")
    private Boolean external;

  public SharingParams() {}

    public SharingParams(Boolean toShare, String trustCircleId, Boolean external) {
    this.toShare = toShare;
    this.trustCircleId = trustCircleId;
        this.external = external;
  }

    public Boolean isToShare() {
    return toShare;
  }

  public void setToShare(Boolean toShare) {
    this.toShare = toShare;
  }

  public String getTrustCircleId() {
    return trustCircleId;
  }

  public void setTrustCircleId(String trustCircleId) {
    this.trustCircleId = trustCircleId;
  }

    public Boolean isExternal() {
        return external;
  }

    public void setExternal(Boolean external) {
        this.external = external;
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
        Objects.equals(this.trustCircleId, sharingParams.trustCircleId) &&
            Objects.equals(this.external, sharingParams.external);
  }

  @Override
  public int hashCode() {
      return Objects.hash(toShare, trustCircleId, external);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharingParams {\n");

    sb.append("    toShare: ").append(toIndentedString(toShare)).append("\n");
    sb.append("    trustCircleId: ").append(toIndentedString(trustCircleId)).append("\n");
      sb.append("    external: ").append(toIndentedString(external)).append("\n");
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

