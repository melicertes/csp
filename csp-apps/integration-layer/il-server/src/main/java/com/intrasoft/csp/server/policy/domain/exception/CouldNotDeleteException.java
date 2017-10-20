package com.intrasoft.csp.server.policy.domain.exception;

import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;

public class CouldNotDeleteException extends CspBusinessException{
    private static final long serialVersionUID = -4238880047981524075L;

    public CouldNotDeleteException(String message) {
        super(message);
    }

    public CouldNotDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
