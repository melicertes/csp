package com.instrasoft.csp.ccs.config.types;


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
}
