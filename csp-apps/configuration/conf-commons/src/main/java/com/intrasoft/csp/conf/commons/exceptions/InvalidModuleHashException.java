package com.intrasoft.csp.conf.commons.exceptions;

import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;

public class InvalidModuleHashException extends CspBusinessException {
    private static final long serialVersionUID = -4855423608073974868L;

    public InvalidModuleHashException(String message) {
        super(message);
    }

    public InvalidModuleHashException(String message, Throwable cause) {
        super(message, cause);
    }
}
