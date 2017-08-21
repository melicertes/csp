package com.intrasoft.csp.commons.exceptions;

import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;

/**
 * Created by iskitsas on 7/10/17.
 */
public class InvalidSharingParamsException extends CspBusinessException {

    private static final long serialVersionUID = -6181067522381549924L;

    public InvalidSharingParamsException(String message) {
        super(message);
    }

    public InvalidSharingParamsException(String message, Throwable cause) {
        super(message, cause);
    }
}
