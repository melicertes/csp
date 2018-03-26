package com.fraunhofer.csp.intelmq.service;

import org.springframework.http.ResponseEntity;

public interface EmitterDataHandler {

	public ResponseEntity<String> handleIntelmqData(String intelmqData, String requestMethod);

}
