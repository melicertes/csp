package com.instrasoft.csp.ccs.config.exception.api;


import com.instrasoft.csp.ccs.config.types.HttpStatusResponseType;

public class HashNotFoundException extends RuntimeException {

    public HashNotFoundException() {
        super(HttpStatusResponseType.API_UPDATE_INVALID_HASH_ENTRY.text());
    }
    public HashNotFoundException(String message) {
        super(message);
    }
    public HashNotFoundException(Throwable cause) {
        super(cause);
    }
    public HashNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
