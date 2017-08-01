package com.intrasoft.csp.anon.client;

import com.intrasoft.csp.anon.commons.exceptions.InvalidDataTypeException;
import com.intrasoft.csp.anon.commons.model.IntegrationAnonData;
import org.springframework.http.ResponseEntity;

/**
 * Created by chris on 14/7/2017.
 */
public interface AnonClient {

    ResponseEntity<String> postAnonData(IntegrationAnonData integrationAnonData, String context) throws InvalidDataTypeException;
}
