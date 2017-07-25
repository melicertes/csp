package com.instrasoft.csp.ccs.config.exception.data;


import com.instrasoft.csp.ccs.config.types.HttpStatusResponseType;

public class ModuleNotRemovableException extends RuntimeException {

    public ModuleNotRemovableException() {
        super(HttpStatusResponseType.API_UPDATE_INVALID_HASH_ENTRY.text());
    }
    public ModuleNotRemovableException(String message) {
        super(message);
    }
    public ModuleNotRemovableException(Throwable cause) {
        super(cause);
    }
    public ModuleNotRemovableException(String message, Throwable cause) {
        super(message, cause);
    }

}
