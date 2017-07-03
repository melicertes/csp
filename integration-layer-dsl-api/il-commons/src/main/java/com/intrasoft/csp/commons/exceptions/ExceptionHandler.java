package com.intrasoft.csp.commons.exceptions;

import org.springframework.web.client.RestClientException;

/**
 * Defines the interface for exception .
 */
public interface ExceptionHandler {

    /**
     * Create an exception with the given name.
     *
     * @param message a String with the exception message .
     * @return any affected statistics.
     */
    RestClientException create(final String message);
}
