package com.intrasoft.csp.conf.commons.types;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum ContactType {
    TECH_ADMIN("tech-admin"),
    CONTACT("contact"),
    SECURITY_OFFICER("securityofficer");

    private final String value;

    ContactType(String value) {
        this.value = value;
    }

    @JsonProperty("contactType")
    public String getValue() {
        return this.value;
    }

    @JsonCreator
    public static ContactType fromValue(@JsonProperty("contactType") String text) {
        for (ContactType b : ContactType.values()) {
            if (String.valueOf(b.getValue()).compareToIgnoreCase(text) == 0) {
                return b;
            }
        }

        // fallback in the case the text presented is the actual enum
        return ContactType.valueOf(text);
    }

}
