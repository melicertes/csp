package com.intrasoft.csp.libraries.restclient.controller;


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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RestExceptionHandlingController extends ResponseEntityExceptionHandler {

    /**
     * Static LOGGER.
     */
//    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandlingController.class);
//
//    @ExceptionHandler({CspCommonException.class})
//    public void handleBadRequests(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
//        LOGGER.error("Bad request: {} from {}, Exception: {} {}",
//                request.getRequestURI(),
//                request.getRemoteHost(),
//                e.getStackTrace()[0].toString(),
//                e.getLocalizedMessage());
//
//        response.sendError(HttpStatus.BAD_REQUEST.value(), e.getLocalizedMessage());
//    }

    @ExceptionHandler(value = {CspCommonException.class})
    public ResponseEntity<Object>  handleBadRequests(WebRequest request,HttpServletRequest req, HttpServletResponse response, Exception e) throws IOException {
        RestErrorDTO restErrorDTO = new RestErrorDTO();

        restErrorDTO.setStatus(String.valueOf(HttpStatus.BAD_REQUEST.value()));
        restErrorDTO.setError(HttpStatus.BAD_REQUEST.name());
        restErrorDTO.setException(e.getClass().getName());
        restErrorDTO.setMessage(e.getLocalizedMessage());
        restErrorDTO.setTimestamp(DateTime.now().toString());
        restErrorDTO.setPath(req.getRequestURI());

        return handleExceptionInternal(e, restErrorDTO, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    protected RestErrorDTO getRestErrorDTO(Exception e, int status, String path){
        RestErrorDTO restErrorDTO = new RestErrorDTO();

        restErrorDTO.setStatus(String.valueOf(status));
        restErrorDTO.setError(HttpStatus.BAD_REQUEST.name());
        restErrorDTO.setException(e.getClass().getName());
        restErrorDTO.setMessage(e.getLocalizedMessage());
        restErrorDTO.setTimestamp(DateTime.now().toString());
        restErrorDTO.setPath(path);

        return restErrorDTO;
    }
}
