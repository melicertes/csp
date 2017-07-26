package com.intrasoft.csp.ccs.config.exception.data;


import com.intrasoft.csp.ccs.config.types.HttpStatusResponseType;

public class InvalidModuleShortNameException extends RuntimeException {

    public InvalidModuleShortNameException() {
        super(HttpStatusResponseType.DATA_MODULE_SAVE_NAME_EXISTS.text());
    }
    public InvalidModuleShortNameException(String message) {
        super(message);
    }
    public InvalidModuleShortNameException(Throwable cause) {
        super(cause);
    }
    public InvalidModuleShortNameException(String message, Throwable cause) {
        super(message, cause);
    }

}
