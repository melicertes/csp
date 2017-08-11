package com.intrasoft.csp.conf.commons.exceptions;

import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;

public class InvalidModuleVersionException extends CspBusinessException {
    private static final long serialVersionUID = 7294632045288736989L;

    public InvalidModuleVersionException(String message) {
        super(message);
    }

    public InvalidModuleVersionException(String message, Throwable cause) {
        super(message, cause);
    }
}
