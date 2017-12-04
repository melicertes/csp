package com.intrasoft.csp.misp.commons.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrganisationWrapper {

    @JsonProperty("Organisation")
    private OrganisationDTO organisation;

    public OrganisationWrapper() {

    }

    public OrganisationDTO getOrganisation() {
        return organisation;
    }

    public void setOrganisationDTO(OrganisationDTO organisationDTO) {
        this.organisation = organisationDTO;
    }
}
