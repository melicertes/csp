package com.instrasoft.csp.ccs.config.exception.data;


import com.instrasoft.csp.ccs.config.types.HttpStatusResponseType;

public class InvalidModuleIdException extends RuntimeException {

    public InvalidModuleIdException() {
        super(HttpStatusResponseType.DATA_INVALID_MODULE_ID.text());
    }
    public InvalidModuleIdException(String message) {
        super(message);
    }
    public InvalidModuleIdException(Throwable cause) {
        super(cause);
    }
    public InvalidModuleIdException(String message, Throwable cause) {
        super(message, cause);
    }

}
