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

  public static final HashMap<IntegrationDataType, String> trustCircleShortName;

  static
  {
      trustCircleShortName = new HashMap<IntegrationDataType, String>();
      trustCircleShortName.put(IntegrationDataType.ARTEFACT, "artefact");
      trustCircleShortName.put(IntegrationDataType.CHAT, "chat");
      trustCircleShortName.put(IntegrationDataType.CONTACT, "contact");
      trustCircleShortName.put(IntegrationDataType.DUMMY, "dummy");
      trustCircleShortName.put(IntegrationDataType.EVENT, "event");
      trustCircleShortName.put(IntegrationDataType.FILE, "file");
      trustCircleShortName.put(IntegrationDataType.INCIDENT, "incident");
      trustCircleShortName.put(IntegrationDataType.THREAT, "threat");
      trustCircleShortName.put(IntegrationDataType.TRUSTCIRCLE, "csp_all");
  }

}



