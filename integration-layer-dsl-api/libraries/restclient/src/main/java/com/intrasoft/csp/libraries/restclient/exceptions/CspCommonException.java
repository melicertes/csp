package com.intrasoft.csp.libraries.restclient.exceptions;

import org.springframework.web.client.RestClientException;

public class CspCommonException extends RestClientException {
    private static final long serialVersionUID = -4448435676701823150L;

    /**
     * Constructor that allows a specific error message to be specified.
     *
     * @param message detail message.
     */
    public CspCommonException(String message) {
        super(message);
    }

    /**
     * Creates a {@code CspCommonException} with the specified
     * detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public CspCommonException(String message, Throwable cause) {
        super(message, cause);
    }
}
