package com.instasoft.csp.ccs.config.exception.data;


import com.instasoft.csp.ccs.config.types.HttpStatusResponseType;

public class InvalidCspIdException extends RuntimeException {

    public InvalidCspIdException() {
        super(HttpStatusResponseType.DATA_INVALID_CSP_ID.text());
    }
    public InvalidCspIdException(String message) {
        super(message);
    }
    public InvalidCspIdException(Throwable cause) {
        super(cause);
    }
    public InvalidCspIdException(String message, Throwable cause) {
        super(message, cause);
    }

}
