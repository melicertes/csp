package com.intrasoft.csp.misp.service;

import com.intrasoft.csp.misp.commons.models.OrganisationDTO;

import java.util.function.Function;

public interface Conversions {
    Function<OrganisationDTO, OrganisationDTO> copyOrganization = (original) -> {
        final OrganisationDTO dto = new OrganisationDTO();
        dto.setAlias(original.getAlias());
//        dto.setAnonymise(original.getAnonymize());
        dto.setContacts(original.getContacts());
        dto.setCreatedBy(original.getCreatedBy());
        dto.setDateCreated(original.getDateCreated());
        dto.setDateModified(original.getDateModified());
        dto.setDescription(original.getDescription());
        dto.setId(original.getId());
        dto.setLandingPage(original.getLandingPage());
//        dto.setLocal(original.getLocal());
        dto.setName(original.getName());
        dto.setNationality(original.getNationality());
        dto.setSector(original.getSector());
        dto.setType(original.getType());
        dto.setUuid(original.getUuid());
        return dto;
    };
}
