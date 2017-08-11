package com.intrasoft.csp.conf.commons.exceptions;

import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;

public class UpdateNotFoundException extends CspBusinessException {
    private static final long serialVersionUID = 8404130711723015226L;

    public UpdateNotFoundException(String message) {
        super(message);
    }

    public UpdateNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
