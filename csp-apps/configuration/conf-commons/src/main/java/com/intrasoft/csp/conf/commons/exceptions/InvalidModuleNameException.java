package com.intrasoft.csp.conf.commons.exceptions;

import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;

public class InvalidModuleNameException extends CspBusinessException {
    private static final long serialVersionUID = -2943194581800481945L;

    public InvalidModuleNameException(String message) {
        super(message);
    }

    public InvalidModuleNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
