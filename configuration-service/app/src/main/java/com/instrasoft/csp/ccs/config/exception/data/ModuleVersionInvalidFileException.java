package com.instrasoft.csp.ccs.config.exception.data;


import com.instrasoft.csp.ccs.config.types.HttpStatusResponseType;

public class ModuleVersionInvalidFileException extends RuntimeException {

    public ModuleVersionInvalidFileException() {
        super(HttpStatusResponseType.DATA_MODULE_VERSION_INVALID_FILE.text());
    }
    public ModuleVersionInvalidFileException(String message) {
        super(message);
    }
    public ModuleVersionInvalidFileException(Throwable cause) {
        super(cause);
    }
    public ModuleVersionInvalidFileException(String message, Throwable cause) {
        super(message, cause);
    }

}
