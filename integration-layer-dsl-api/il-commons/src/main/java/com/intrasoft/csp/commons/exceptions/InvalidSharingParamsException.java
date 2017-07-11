package com.intrasoft.csp.commons.exceptions;

/**
 * Created by iskitsas on 7/10/17.
 */
public class InvalidSharingParamsException extends CspBusinessException {
    private static final long serialVersionUID = 5330738780392959977L;

    public InvalidSharingParamsException(String message) {
        super(message);
    }

    public InvalidSharingParamsException(String message, Throwable cause) {
        super(message, cause);
    }
}
