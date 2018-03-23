package com.intrasoft.csp.client;

public enum CspDataMappingType {

    EVENT("event"),
    THREAT("threat"),
    VULNERABILITY("vulnerability"),
    INCIDENT("incident"),
    ARTEFACT("artefact"),
    ALL("all");

    private final String  value;

    CspDataMappingType(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

}
