package com.instasoft.csp.ccs.config.types;


import com.fasterxml.jackson.annotation.JsonCreator;

public enum ContactType {
    TECH_ADMIN("tech-admin"),
    CONTACT("contact"),
    SECURITY_OFFICER("securityofficer");

    private final String value;

    ContactType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @JsonCreator
    public static ContactType fromValue(String text) {
        for (ContactType b : ContactType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

}
