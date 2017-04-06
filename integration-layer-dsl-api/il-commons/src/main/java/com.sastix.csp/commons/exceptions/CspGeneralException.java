package com.sastix.csp.commons.exceptions;

/**
 * Created by iskitsas on 4/4/17.
 */
public class CspGeneralException extends CspBusinessException {
    public CspGeneralException(String message) {
        super(message);
    }

    public CspGeneralException(String message, Throwable cause) {
        super(message, cause);
    }
}
