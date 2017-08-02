package com.intrasoft.csp.ccs.commons.exceptions;

public class InvalidDataTypeException extends CspBusinessException {

    private static final long serialVersionUID = -7035022160559563259L;


    public InvalidDataTypeException(String message) {
        super(message);
    }

    public InvalidDataTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
