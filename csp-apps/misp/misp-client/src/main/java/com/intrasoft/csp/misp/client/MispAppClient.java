package com.intrasoft.csp.misp.client;

import com.intrasoft.csp.misp.commons.models.OrganisationDTO;
import com.intrasoft.csp.misp.commons.models.generated.SharingGroup;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MispAppClient {
	void setProtocolHostPortHeaders(String protocol, String host, String port, String authorizationKey);

	String getContext();

	ResponseEntity<Object> getMispEvent(String uuid);
	ResponseEntity<String> addMispEvent(String object);
	ResponseEntity<String> updateMispEvent(String object);
	ResponseEntity<String> updateMispEvent(String uuid, String object) ;
	ResponseEntity<String> deleteMispEvent(String uuid);
	ResponseEntity<String> addMispProposal(String id, String body);
	ResponseEntity<String> updateMispProposal(String object);
	ResponseEntity<String> deleteMispProposal(String object);
	ResponseEntity<String> updateMispProposal(String id, String object);
	ResponseEntity<String> getMispAttribute(String uuid);
	ResponseEntity<String> postMispAttribute(String uuid);
	OrganisationDTO getMispOrganisation(String uuid);
	List<OrganisationDTO> getAllMispOrganisations();
	OrganisationDTO addMispOrganisation(OrganisationDTO organisationDTO);
//	TODO: Implementation of updating MISP Organisations is partially working until REST API problem is solved.
//  MISP's Organisation API updates only the "name" field and it has been reported; waiting for feedback.
	OrganisationDTO updateMispOrganisation(OrganisationDTO organisationDTO);
	// Organisations can't be deleted when they reference users or are referenced themselves by misp events
	// Returns true when organisation is successfully deleted
	boolean deleteMispOrganisation(String id);

	List<SharingGroup> getAllMispSharingGroups();
    SharingGroup getMispSharingGroup(String uuid);
    SharingGroup addMispSharingGroup(SharingGroup sharingGroup);
    SharingGroup updateMispSharingGroup(SharingGroup sharingGroup);
    // The use of ids instead of uuids is supported by the API when adding/removing Organisations to/from the Sharing Groups
    boolean updateMispSharingGroupAddOrganisation(String sharingGroupUuid, String organisationUuid);
    boolean updateMispSharingGroupRemoveOrganisation(String sharingGroupUuid, String organisationUuid);
    Boolean deleteMispSharingGroup(String id);
}
