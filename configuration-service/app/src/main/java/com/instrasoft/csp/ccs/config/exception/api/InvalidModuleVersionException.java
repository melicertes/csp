package com.instrasoft.csp.ccs.config.exception.api;


import com.instrasoft.csp.ccs.config.types.HttpStatusResponseType;

public class InvalidModuleVersionException extends RuntimeException {

    public InvalidModuleVersionException() {
        super(HttpStatusResponseType.API_INVALID_MODULE_VERSION.text());
    }
    public InvalidModuleVersionException(String message) {
        super(message);
    }
    public InvalidModuleVersionException(Throwable cause) {
        super(cause);
    }
    public InvalidModuleVersionException(String message, Throwable cause) {
        super(message, cause);
    }

}
