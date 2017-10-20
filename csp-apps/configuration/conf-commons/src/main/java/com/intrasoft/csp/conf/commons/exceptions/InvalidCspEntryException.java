package com.intrasoft.csp.conf.commons.exceptions;

import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;

public class InvalidCspEntryException extends CspBusinessException {
    private static final long serialVersionUID = -7332863252678424816L;

    public InvalidCspEntryException(String message) {
        super(message);
    }

    public InvalidCspEntryException(String message, Throwable cause) {
        super(message, cause);
    }
}
