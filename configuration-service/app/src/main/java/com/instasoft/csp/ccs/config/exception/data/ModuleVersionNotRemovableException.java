package com.instasoft.csp.ccs.config.exception.data;


import com.instasoft.csp.ccs.config.types.HttpStatusResponseType;

public class ModuleVersionNotRemovableException extends RuntimeException {

    public ModuleVersionNotRemovableException() {
        super(HttpStatusResponseType.DATA_MODULE_VERSION_DELETE_ERROR.text());
    }
    public ModuleVersionNotRemovableException(String message) {
        super(message);
    }
    public ModuleVersionNotRemovableException(Throwable cause) {
        super(cause);
    }
    public ModuleVersionNotRemovableException(String message, Throwable cause) {
        super(message, cause);
    }

}
