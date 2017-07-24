package com.intrasoft.csp.client;


//import com.intrasoft.csp.commons.model.IntegrationAnonData;

import com.intrasoft.csp.anon.model.IntegrationAnonData;
import com.intrasoft.csp.commons.exceptions.InvalidDataTypeException;
import com.intrasoft.csp.commons.model.IntegrationData;
import org.springframework.http.ResponseEntity;

/**
 * Created by chris on 14/7/2017.
 */
public interface AnonClient {

    void setProtocolHostPort(String protocol, String host, String port);

    String getContext();

    ResponseEntity<String> postAnonData(IntegrationData integrationData, String context) throws InvalidDataTypeException;
}
