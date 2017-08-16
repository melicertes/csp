package com.intrasoft.csp.anon.commons.exceptions;

import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;

public class MalformedIntegrationDataStructureException extends CspBusinessException {

    public MalformedIntegrationDataStructureException(String message) {
        super(message);
    }

    public MalformedIntegrationDataStructureException(String message, Throwable cause) {
        super(message, cause);
    }
}
