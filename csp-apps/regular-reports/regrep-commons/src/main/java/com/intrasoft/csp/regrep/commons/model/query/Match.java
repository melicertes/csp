package com.intrasoft.csp.regrep.commons.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Match {

    @JsonProperty("logtype")
    private String logType;

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    @Override
    public String toString() {
        return "Match{" +
                "logType='" + logType + '\'' +
                '}';
    }
}
