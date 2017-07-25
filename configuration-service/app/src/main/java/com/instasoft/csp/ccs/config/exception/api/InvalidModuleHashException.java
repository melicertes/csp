package com.instasoft.csp.ccs.config.exception.api;


import com.instasoft.csp.ccs.config.types.HttpStatusResponseType;

public class InvalidModuleHashException extends RuntimeException {

    public InvalidModuleHashException() {
        super(HttpStatusResponseType.API_INVALID_MODULE_HASH.text());
    }
    public InvalidModuleHashException(String message) {
        super(message);
    }
    public InvalidModuleHashException(Throwable cause) {
        super(cause);
    }
    public InvalidModuleHashException(String message, Throwable cause) {
        super(message, cause);
    }

}
