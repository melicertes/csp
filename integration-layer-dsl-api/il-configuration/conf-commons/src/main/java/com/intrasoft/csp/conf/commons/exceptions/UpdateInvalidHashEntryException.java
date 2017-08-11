package com.intrasoft.csp.conf.commons.exceptions;

import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;

public class UpdateInvalidHashEntryException extends CspBusinessException{
    private static final long serialVersionUID = -8853361324605350710L;

    public UpdateInvalidHashEntryException(String message) {
        super(message);
    }

    public UpdateInvalidHashEntryException(String message, Throwable cause) {
        super(message, cause);
    }
}
