package com.intrasoft.csp.commons.exceptions;

import org.springframework.web.client.RestClientException;

public class ErrorLogException extends RestClientException {
    private static final long serialVersionUID = -7182852157480642296L;

    public ErrorLogException(String msg) {
        super(msg);
    }

    public ErrorLogException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
