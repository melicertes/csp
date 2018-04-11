package com.intrasoft.csp.regrep;

public enum CspDataMappingType {

    EVENT("event"),
    THREAT("threat"),
    VULNERABILITY("vulnerability"),
    INCIDENT("incident"),
    ARTEFACT("artefact"),
    FILE("file"),
    ALL("all");

    private final String  value;

    CspDataMappingType(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public String beautify() {
        String name = this.name();
        return name.substring(0,1) + name.substring(1,name.length()).toLowerCase();
    }

}
