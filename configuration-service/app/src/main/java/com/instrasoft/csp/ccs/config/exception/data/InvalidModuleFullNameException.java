package com.instrasoft.csp.ccs.config.exception.data;


import com.instrasoft.csp.ccs.config.types.HttpStatusResponseType;

public class InvalidModuleFullNameException extends RuntimeException {

    public InvalidModuleFullNameException() {
        super(HttpStatusResponseType.DATA_MODULE_VERSION_NAME_EXISTS.text());
    }
    public InvalidModuleFullNameException(String message) {
        super(message);
    }
    public InvalidModuleFullNameException(Throwable cause) {
        super(cause);
    }
    public InvalidModuleFullNameException(String message, Throwable cause) {
        super(message, cause);
    }

}
