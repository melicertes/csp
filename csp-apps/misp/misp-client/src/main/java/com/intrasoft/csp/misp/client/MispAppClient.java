package com.intrasoft.csp.misp.client;

import com.intrasoft.csp.misp.commons.models.OrganisationDTO;
import com.intrasoft.csp.misp.commons.models.OrganisationWrapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface MispAppClient {
	void setProtocolHostPortHeaders(String protocol, String host, String port, String authorizationKey);

	String getContext();

	ResponseEntity<String> getMispEvent(String uuid);
	ResponseEntity<String> addMispEvent(String object);
	ResponseEntity<String> updateMispEvent(String object);
	ResponseEntity<String> updateMispEvent(String uuid, String object) ;
	ResponseEntity<String> deleteMispEvent(String uuid);

	OrganisationDTO getMispOrganisation(String uuid);
	OrganisationDTO addMispOrganisation(OrganisationDTO organisationDTO);
//	TODO: Implementation of updating MISP Organisations is partially working until REST API problem is solved.
//  MISP's Organisation API updates only the "name" field and it has been reported; waiting for feedback.
	OrganisationDTO updateMispOrganisation(OrganisationDTO organisationDTO);

	// Returns the successfully deleted organisation as object, otherwise returns null
	OrganisationDTO deleteMispOrganisation(String id);

//	TODO: There isn't any MISP REST API support for full CRUD operations on Sharing Groups yet.


}
