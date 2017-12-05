package com.intrasoft.csp.misp.commons.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SharingGroupWrapper {

    @JsonProperty("SharingGroup")
    private SharingGroupDTO sharingGroup;

    public SharingGroupWrapper() {

    }

    public SharingGroupDTO getSharingGroup() {
        return sharingGroup;
    }

    public void setSharingGroup(SharingGroupDTO sharingGroup) {
        this.sharingGroup = sharingGroup;
    }
}
