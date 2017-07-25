package com.intrasoft.csp.ccs.config.exception.data;


import com.intrasoft.csp.ccs.config.types.HttpStatusResponseType;

public class CspIdExistsException extends RuntimeException {

    public CspIdExistsException() {
        super(HttpStatusResponseType.DATA_CSP_SAVE_RECORD_EXISTS.text());
    }
    public CspIdExistsException(String message) {
        super(message);
    }
    public CspIdExistsException(Throwable cause) {
        super(cause);
    }
    public CspIdExistsException(String message, Throwable cause) {
        super(message, cause);
    }

}
