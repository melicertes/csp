package com.intrasoft.csp.anon.server.exception;


import com.intrasoft.csp.anon.commons.exceptions.AnonException;
import com.intrasoft.csp.libraries.restclient.model.RestErrorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class AnonExceptionHandlingController extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnonExceptionHandlingController.class);

    @ExceptionHandler(value = { AnonException.class})
    protected ResponseEntity<Object> handleInvalidDatatype(RuntimeException ex, WebRequest request) {
        RestErrorDTO restErrorDTO = new RestErrorDTO();
        restErrorDTO.setError(HttpStatus.BAD_REQUEST.name());
        restErrorDTO.setMessage(ex.getMessage());
        restErrorDTO.setStatus(String.valueOf(HttpStatus.BAD_REQUEST.value()));
        restErrorDTO.setException(ex.getMessage());
        restErrorDTO.setTimestamp(String.valueOf(new Date().getTime()));
        return handleExceptionInternal(ex, restErrorDTO,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
