package com.intrasoft.csp.ccs.config.exception.data;


import com.intrasoft.csp.ccs.config.types.HttpStatusResponseType;

public class InvalidCspContactException extends RuntimeException {

    public InvalidCspContactException() {
        super(HttpStatusResponseType.DATA_CSP_SAVE_RECORD_EXISTS.text());
    }
    public InvalidCspContactException(String message) {
        super(message);
    }
    public InvalidCspContactException(Throwable cause) {
        super(cause);
    }
    public InvalidCspContactException(String message, Throwable cause) {
        super(message, cause);
    }

}
