package com.sastix.csp.commons.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 27/3/2017.
 */
public class TrustCircle implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("cspId")
    private List<String> csps = new ArrayList<>();

    public TrustCircle() {
    }

    public TrustCircle(List<String> csps) {
        this.csps = csps;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public List<String> getCsps() {
        return csps;
    }

    public void setCsps(List<String> csps) {
        this.csps = csps;
    }

    @Override
    public String toString() {
        return "TrustCircle{" +
                "csps=" + csps +
                '}';
    }
}
