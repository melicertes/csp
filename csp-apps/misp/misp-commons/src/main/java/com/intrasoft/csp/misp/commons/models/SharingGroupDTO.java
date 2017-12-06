package com.intrasoft.csp.misp.commons.models;

import java.util.ArrayList;
import java.util.List;

public class SharingGroupDTO {

    private String id;
    private String name;
    private String description;
    private String releasability;
    private boolean local;
    private boolean active;

    private OrganisationWrapper organisationWrapper;



    private List<String> organisationsUuids = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReleasability() {
        return releasability;
    }

    public void setReleasability(String releasability) {
        this.releasability = releasability;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
