package com.intrasoft.csp.conf.commons.exceptions;


import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;

public class ConfException extends CspBusinessException {

    private static final long serialVersionUID = 2580556814256005621L;
    private Integer code;


//    public ConfException(Integer code) {
//        this.code = code;
//    }

    public ConfException(String message) {
        super(message);
    }

    public ConfException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public ConfException(String message, Throwable cause, Integer code) {
        super(message, cause);
        this.code = code;
    }

//    public ConfException(Throwable cause, Integer code) {
//        super(cause);
//        this.code = code;
//    }
//
//    public ConfException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Integer code) {
//        super(message, cause, enableSuppression, writableStackTrace);
//        this.code = code;
//    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
