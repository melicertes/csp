package com.sastix.csp.model;

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

  @JsonProperty("isExternal")
  private Boolean isExternal;

  public SharingParams() {}

  public SharingParams(Boolean toShare, String trustCircleId, Boolean isExternal) {
    this.toShare = toShare;
    this.trustCircleId = trustCircleId;
    this.isExternal = isExternal;
  }

   /**
   * Get toShare
   * @return toShare
  **/
  public Boolean getToShare() {
    return toShare;
  }

  public void setToShare(Boolean toShare) {
    this.toShare = toShare;
  }

   /**
   * Get trustCircleId
   * @return trustCircleId
  **/
  public String getTrustCircleId() {
    return trustCircleId;
  }

  public void setTrustCircleId(String trustCircleId) {
    this.trustCircleId = trustCircleId;
  }

   /**
   * Get isExternal
   * @return isExternal
  **/
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
        Objects.equals(this.trustCircleId, sharingParams.trustCircleId) &&
        Objects.equals(this.isExternal, sharingParams.isExternal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(toShare, trustCircleId, isExternal);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharingParams {\n");

    sb.append("    toShare: ").append(toIndentedString(toShare)).append("\n");
    sb.append("    trustCircleId: ").append(toIndentedString(trustCircleId)).append("\n");
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

