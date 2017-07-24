package com.intrasoft.csp.client;

import com.intrasoft.csp.commons.exceptions.InvalidDataTypeException;
import com.intrasoft.csp.commons.model.IntegrationData;
import org.springframework.http.ResponseEntity;

/**
 * Created by chris on 14/7/2017.
 */
public interface AnonClient {

    ResponseEntity<String> postAnonData(IntegrationData integrationData, String context) throws InvalidDataTypeException;
}
