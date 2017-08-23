package com.intrasoft.csp.libraries.versioning.exceptions;

/**
 * Runtime Exception for unsupported REST API version.
 */
public class VersionNotSupportedException extends RuntimeException {


    private static final long serialVersionUID = 4459404229489080587L;

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
