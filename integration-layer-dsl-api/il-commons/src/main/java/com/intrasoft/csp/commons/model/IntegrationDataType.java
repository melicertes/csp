package com.intrasoft.csp.commons.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;

/**
 * Gets or Sets IntegrationDataType
 */
public enum IntegrationDataType {

  DUMMY("dummy"),

  THREAT("threat"),

  EVENT("event"),

  ARTEFACT("artefact"),

  INCIDENT("incident"),

  CONTACT("contact"),

  FILE("file"),

  CHAT("chat"),

  VULNERABILITY("vulnerability"),

  TRUSTCIRCLE("csp_all");

  private String value;

  IntegrationDataType(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static IntegrationDataType fromValue(String text) {
    for (IntegrationDataType b : IntegrationDataType.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}



