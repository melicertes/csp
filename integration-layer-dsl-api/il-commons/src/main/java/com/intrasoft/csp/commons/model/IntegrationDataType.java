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

  public static final HashMap<IntegrationDataType, String> tcNamingConventionForShortName;

  static
  {
    tcNamingConventionForShortName = new HashMap<>();
    tcNamingConventionForShortName.put(IntegrationDataType.VULNERABILITY, "CTC::SHARING_DATA_VULNERABILITY");
    tcNamingConventionForShortName.put(IntegrationDataType.ARTEFACT, "CTC::SHARING_DATA_ARTEFACT");
    tcNamingConventionForShortName.put(IntegrationDataType.CHAT, "CTC::SHARING_DATA_CHAT");
    tcNamingConventionForShortName.put(IntegrationDataType.CONTACT, "CTC::SHARING_DATA_CONTACT");
    tcNamingConventionForShortName.put(IntegrationDataType.DUMMY, "dummy");
    tcNamingConventionForShortName.put(IntegrationDataType.EVENT, "CTC::SHARING_DATA_EVENT");
    tcNamingConventionForShortName.put(IntegrationDataType.FILE, "CTC::SHARING_DATA_FILE");
    tcNamingConventionForShortName.put(IntegrationDataType.INCIDENT, "CTC::SHARING_DATA_INCIDENT");
    tcNamingConventionForShortName.put(IntegrationDataType.THREAT, "CTC::SHARING_DATA_THREAT");
    tcNamingConventionForShortName.put(IntegrationDataType.TRUSTCIRCLE, "CTC::CSP_ALL");
  }
}



