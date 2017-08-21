package com.intrasoft.csp.anon.commons.exceptions;

import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;

public class AnonException extends CspBusinessException {

    private static final long serialVersionUID = -4881309538442142324L;

    public AnonException(String message) {
        super(message);
    }

    public AnonException(String message, Throwable cause) {
        super(message, cause);
    }
}
