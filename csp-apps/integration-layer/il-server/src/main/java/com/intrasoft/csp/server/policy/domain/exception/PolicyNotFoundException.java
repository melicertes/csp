package com.intrasoft.csp.server.policy.domain.exception;

import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;

public class PolicyNotFoundException extends CspBusinessException{
    private static final long serialVersionUID = -36802008376128225L;

    public PolicyNotFoundException(String message) {
        super(message);
    }

    public PolicyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
