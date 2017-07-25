package com.instasoft.csp.ccs.config.exception.data;


import com.instasoft.csp.ccs.config.types.HttpStatusResponseType;

public class ModuleVersionExistsException extends RuntimeException {

    public ModuleVersionExistsException() {
        super(HttpStatusResponseType.DATA_MODULE_VERSION_EXISTS.text());
    }
    public ModuleVersionExistsException(String message) {
        super(message);
    }
    public ModuleVersionExistsException(Throwable cause) {
        super(cause);
    }
    public ModuleVersionExistsException(String message, Throwable cause) {
        super(message, cause);
    }

}
