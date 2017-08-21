package com.intrasoft.csp.commons.exceptions;

import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;

/**
 * Created by iskitsas on 5/6/17.
 */
public class TcException extends CspBusinessException {
    private static final long serialVersionUID = 1397774502827430075L;

    public TcException(String message) {
        super(message);
    }

    public TcException(String message, Throwable cause) {
        super(message, cause);
    }
}
