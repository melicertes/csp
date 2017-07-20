package com.instrasoft.csp.ccs.config.exception.api;


import com.instrasoft.csp.ccs.config.types.HttpStatusResponseType;

public class InvalidModuleNameException extends RuntimeException {

    public InvalidModuleNameException() {
        super(HttpStatusResponseType.API_INVALID_MODULE_NAME.text());
    }
    public InvalidModuleNameException(String message) {
        super(message);
    }
    public InvalidModuleNameException(Throwable cause) {
        super(cause);
    }
    public InvalidModuleNameException(String message, Throwable cause) {
        super(message, cause);
    }

}
