package com.intrasoft.csp.regrep;

public enum LogstashMappingType {

    AUDIT("aud"),
    EXCEPTION("exc"),
    ALL("all");  // Simply removes "match" node from query

    private final String value;

    LogstashMappingType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public String beautify() {
        String name = this.name();
        return name.substring(0,1) + name.substring(1,name.length()).toLowerCase();
    }
}
