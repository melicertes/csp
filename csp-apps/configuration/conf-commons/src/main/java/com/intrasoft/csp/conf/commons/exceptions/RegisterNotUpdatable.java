package com.intrasoft.csp.conf.commons.exceptions;

import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;

public class RegisterNotUpdatable extends CspBusinessException {
    private static final long serialVersionUID = 1859481416833043599L;

    public RegisterNotUpdatable(String message) {
        super(message);
    }

    public RegisterNotUpdatable(String message, Throwable cause) {
        super(message, cause);
    }
}
