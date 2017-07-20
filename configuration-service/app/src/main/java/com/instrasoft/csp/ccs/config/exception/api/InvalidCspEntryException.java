package com.instrasoft.csp.ccs.config.exception.api;


import com.instrasoft.csp.ccs.config.types.HttpStatusResponseType;

public class InvalidCspEntryException extends RuntimeException {

    public InvalidCspEntryException() {
        super(HttpStatusResponseType.API_INVALID_CSP_ENTRY.text());
    }
    public InvalidCspEntryException(String message) {
        super(message);
    }
    public InvalidCspEntryException(Throwable cause) {
        super(cause);
    }
    public InvalidCspEntryException(String message, Throwable cause) {
        super(message, cause);
    }
}
