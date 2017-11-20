package com.intrasoft.csp.libraries.restclient.exceptions;

import org.springframework.http.HttpHeaders;

public class StatusCodeException extends RuntimeException{
    private static final long serialVersionUID = 3823098779222063071L;

    Integer statusCode;
    HttpHeaders httpHeaders;

    public StatusCodeException(String message, Integer statusCode, HttpHeaders httpHeaders) {
        super(message);
        this.statusCode = statusCode;
        this.httpHeaders = httpHeaders;
    }

    public StatusCodeException(String message, Throwable cause, Integer statusCode, HttpHeaders httpHeaders) {
        super(message, cause);
        this.statusCode = statusCode;
        this.httpHeaders = httpHeaders;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

}
