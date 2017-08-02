package com.intrasoft.csp.libraries.restclient.controller;


import com.intrasoft.csp.libraries.restclient.exceptions.CspCommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class RestExceptionHandlingController {

    /**
     * Static LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandlingController.class);

    @ExceptionHandler({CspCommonException.class})
    public void handleBadRequests(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
        LOGGER.error("Bad request: {} from {}, Exception: {} {}",
                request.getRequestURI(),
                request.getRemoteHost(),
                e.getStackTrace()[0].toString(),
                e.getLocalizedMessage());

        response.sendError(HttpStatus.BAD_REQUEST.value(), e.getLocalizedMessage());
    }
}
