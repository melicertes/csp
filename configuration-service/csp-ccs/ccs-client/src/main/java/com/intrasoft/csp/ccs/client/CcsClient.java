package com.intrasoft.csp.ccs.client;


import com.intrasoft.csp.ccs.commons.exceptions.InvalidDataTypeException;
import org.springframework.http.ResponseEntity;

public interface CcsClient {

    ResponseEntity getUpdates(String cspId) throws InvalidDataTypeException;

}
