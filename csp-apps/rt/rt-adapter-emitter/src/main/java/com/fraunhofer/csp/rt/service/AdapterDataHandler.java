package com.fraunhofer.csp.rt.service;

import org.springframework.http.ResponseEntity;

import com.intrasoft.csp.commons.model.IntegrationData;

/**
 * Created by Majid Salehi on 4/8/17.
 */
public interface AdapterDataHandler {

	public ResponseEntity<String> handleIntegrationData(IntegrationData integrationData, String requestMethod);
}
