package com.intrasoft.csp.conf.server.controller;


import com.intrasoft.csp.conf.commons.context.ApiContextUrl;
import com.intrasoft.csp.conf.commons.exceptions.*;
import com.intrasoft.csp.conf.commons.types.StatusResponseType;
import com.intrasoft.csp.conf.server.context.DataContextUrl;
import com.intrasoft.csp.libraries.restclient.controller.RestExceptionHandlingController;
import com.intrasoft.csp.libraries.restclient.exceptions.CspCommonException;
import com.intrasoft.csp.libraries.restclient.model.RestErrorDTO;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;


@ControllerAdvice
public class CspCcsExceptionHandlingController extends RestExceptionHandlingController {

    private static Logger LOG_EXCEPTION = LoggerFactory.getLogger("exc-log");

    @ExceptionHandler(value = {InvalidCspEntryException.class})
    public ResponseEntity<Object>  invalidCspEntryException(WebRequest request,Exception e) throws IOException {
        return handleException(e,StatusResponseType.API_INVALID_CSP_ENTRY.code(),HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {InvalidModuleHashException.class})
    public ResponseEntity<Object>  invalidModuleHashException(WebRequest request, Exception e) throws IOException {
        return handleException(e,StatusResponseType.API_INVALID_MODULE_HASH.code(),HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {InvalidModuleNameException.class})
    public ResponseEntity<Object>  invalidModuleNameException(WebRequest request, Exception e) throws IOException {
        return handleException(e,StatusResponseType.API_INVALID_MODULE_NAME.code(),HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {InvalidModuleVersionException.class})
    public ResponseEntity<Object>  invalidModuleVersionException(WebRequest request, Exception e) throws IOException {
        return handleException(e,StatusResponseType.API_INVALID_MODULE_VERSION.code(),HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {RegisterNotUpdatable.class})
    public ResponseEntity<Object>  registerNotUpdatable(WebRequest request, Exception e) throws IOException {
        return handleException(e,StatusResponseType.API_REGISTER_NOT_UPDATABLE.code(),HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {UpdateInvalidHashEntryException.class})
    public ResponseEntity<Object>  updateInvalidHashEntryException(WebRequest request, Exception e) throws IOException {
        return handleException(e,StatusResponseType.API_UPDATE_INVALID_HASH_ENTRY.code(),HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {UpdateNotFoundException.class})
    public ResponseEntity<Object>  updateNotFoundException(WebRequest request, Exception e) throws IOException {
        return handleException(e,StatusResponseType.API_UPDATE_NOT_FOUND.code(),HttpStatus.BAD_REQUEST, request);
    }
}
