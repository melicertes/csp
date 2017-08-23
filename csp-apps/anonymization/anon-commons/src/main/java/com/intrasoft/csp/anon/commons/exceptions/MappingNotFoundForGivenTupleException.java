package com.intrasoft.csp.anon.commons.exceptions;

import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;

public class MappingNotFoundForGivenTupleException extends CspBusinessException{
    private static final long serialVersionUID = -2860038853030711989L;

    public MappingNotFoundForGivenTupleException(String message) {
        super(message);
    }

    public MappingNotFoundForGivenTupleException(String message, Throwable cause) {
        super(message, cause);
    }
}
