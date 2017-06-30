package com.intrasoft.csp.commons.exceptions;

public class CspBusinessException extends CspCommonException {
    private static final long serialVersionUID = -5693105063994495528L;

    /**
     * Constructor that allows a specific error message to be specified.
     *
     * @param message detail message.
     */
    public CspBusinessException(String message) {
        super(message);
    }

    /**
     * Creates a {@code CspBusinessException} with the specified
     * detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public CspBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
