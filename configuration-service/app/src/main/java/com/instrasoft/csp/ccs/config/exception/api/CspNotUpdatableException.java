package com.instrasoft.csp.ccs.config.exception.api;


import com.instrasoft.csp.ccs.config.types.HttpStatusResponseType;

public class CspNotUpdatableException extends RuntimeException {

    public CspNotUpdatableException() {
        super(HttpStatusResponseType.API_REGISTER_NOT_UPDATABLE.text());
    }
    public CspNotUpdatableException(String message) {
        super(message);
    }
    public CspNotUpdatableException(Throwable cause) {
        super(cause);
    }
    public CspNotUpdatableException(String message, Throwable cause) {
        super(message, cause);
    }

}
