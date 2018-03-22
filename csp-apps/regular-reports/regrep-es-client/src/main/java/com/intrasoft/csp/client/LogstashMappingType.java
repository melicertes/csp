package com.intrasoft.csp.client;

public enum LogstashMappingType {

    AUDIT("aud"),
    EXCEPTION("exc");

    private final String value;

    LogstashMappingType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
