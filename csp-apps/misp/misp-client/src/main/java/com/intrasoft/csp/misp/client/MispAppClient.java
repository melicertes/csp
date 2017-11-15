package com.intrasoft.csp.misp.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface MispAppClient {
	void setProtocolHostPortHeaders(String protocol, String host, String port, String authorizationKey);

	String getContext();

	ResponseEntity<String> addMispEvent(String object);
	ResponseEntity<String> updateMispEvent(String uuid, String object) throws IOException;
	ResponseEntity<String> deleteMispEvent(String uuid);

}