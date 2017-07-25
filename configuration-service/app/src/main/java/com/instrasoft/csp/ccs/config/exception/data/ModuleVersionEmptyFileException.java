package com.instrasoft.csp.ccs.config.exception.data;


import com.instrasoft.csp.ccs.config.types.HttpStatusResponseType;

public class ModuleVersionEmptyFileException extends RuntimeException {

    public ModuleVersionEmptyFileException() {
        super(HttpStatusResponseType.DATA_MODULE_VERSION_EMPTY_FILE.text());
    }
    public ModuleVersionEmptyFileException(String message) {
        super(message);
    }
    public ModuleVersionEmptyFileException(Throwable cause) {
        super(cause);
    }
    public ModuleVersionEmptyFileException(String message, Throwable cause) {
        super(message, cause);
    }

}
