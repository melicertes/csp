package com.intrasoft.csp.ccs.config;


public class CspCcsException extends RuntimeException {

    private Integer code;


    public CspCcsException(Integer code) {
        this.code = code;
    }

    public CspCcsException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public CspCcsException(String message, Throwable cause, Integer code) {
        super(message, cause);
        this.code = code;
    }

    public CspCcsException(Throwable cause, Integer code) {
        super(cause);
        this.code = code;
    }

    public CspCcsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Integer code) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
