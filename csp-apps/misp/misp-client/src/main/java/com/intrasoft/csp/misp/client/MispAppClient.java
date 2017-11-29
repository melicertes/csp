package com.intrasoft.csp.misp.client;

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

	ResponseEntity<String> getMispOrganisation(String uuid);
	ResponseEntity<String> addMispOrganisation(String object);
//	TODO: Implementations of updating Misp Organisations are on hold; MISP's API doesn't update values for fields other than "name".
	ResponseEntity<String> updateMispOrganisation(String object);
	ResponseEntity<String> updateMispOrganisation(String uuid, String object) ;
	ResponseEntity<String> deleteMispOrganisation(String id);

//	TODO: Can't find any MISP REST API for full CRUD operations on Sharing Groups yet; investigation in progress.
	ResponseEntity<String> getAllMispSharingGroups();

}
