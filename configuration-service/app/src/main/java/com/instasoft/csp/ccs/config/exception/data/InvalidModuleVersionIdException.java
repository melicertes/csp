package com.instasoft.csp.ccs.config.exception.data;

import com.instasoft.csp.ccs.config.types.HttpStatusResponseType;


public class InvalidModuleVersionIdException extends RuntimeException {

    public InvalidModuleVersionIdException() {
        super(HttpStatusResponseType.DATA_INVALID_MODULE_VERSION_ID.text());
    }
    public InvalidModuleVersionIdException(String message) {
        super(message);
    }
    public InvalidModuleVersionIdException(Throwable cause) {
        super(cause);
    }
    public InvalidModuleVersionIdException(String message, Throwable cause) {
        super(message, cause);
    }

}
