package com.intrasoft.csp.anon.commons.exceptions;

import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;

public class UnsupportedDataTypeException extends CspBusinessException {


    private static final long serialVersionUID = 688618983639049736L;

    public UnsupportedDataTypeException(String message) {
        super(message);
    }

    public UnsupportedDataTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
