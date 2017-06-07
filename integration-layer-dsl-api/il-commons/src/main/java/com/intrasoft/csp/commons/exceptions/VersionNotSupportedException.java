package com.intrasoft.csp.commons.exceptions;

/**
 * Runtime Exception for unsupported REST API version.
 */
public class VersionNotSupportedException extends RuntimeException
{
    private static final long serialVersionUID = -908063904262705775L;

    /** Constructs a new runtime exception with {@code null} as its
     * detail message.
     */
    public VersionNotSupportedException() {
        super();
    }

    /** Constructs a new runtime exception with the specified detail message.
     *
     * @param   message   the detail message.
     */
    public VersionNotSupportedException(String message) {
        super(message);
    }
}
