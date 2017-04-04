package com.sastix.csp.commons.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets IntegrationDataType
 */
public enum IntegrationDataType {

  EVENT("event"),

  THREAT("threat"),
  
  INCIDENT("incident"),
  
  VULNERABILITY("vulnerability"),
  
  ARTEFACT("artefact"),
  
  CHAT("chat"),
  
  FILE("file"),

  CONTACT("contact"),

  TRUSTCIRCLE("trustCircle");

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

