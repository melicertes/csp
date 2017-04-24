package com.sastix.csp.commons.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by chris on 27/3/2017.
 */
public class Csp implements Serializable {

    @JsonProperty("cspId")
    private Integer cspId;

    public Csp() {
    }

    public Csp(Integer cspId) {
        this.cspId = cspId;
    }

    public Integer getCspId() {
        return cspId;
    }

    public void setCspId(Integer cspId) {
        this.cspId = cspId;
    }

    @Override
    public String toString() {
        return "Csp{" +
                "cspId='" + cspId + '\'' +
                '}';
    }
}
