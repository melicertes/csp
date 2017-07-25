package com.instasoft.csp.ccs.config.exception.data;

import com.instasoft.csp.ccs.config.types.HttpStatusResponseType;


public class ModuleVersionHashExistsException extends RuntimeException {

    public ModuleVersionHashExistsException() {
        super(HttpStatusResponseType.DATA_MODULE_VERSION_HASH_EXISTS.text());
    }
    public ModuleVersionHashExistsException(String message) {
        super(message);
    }
    public ModuleVersionHashExistsException(Throwable cause) {
        super(cause);
    }
    public ModuleVersionHashExistsException(String message, Throwable cause) {
        super(message, cause);
    }

}
