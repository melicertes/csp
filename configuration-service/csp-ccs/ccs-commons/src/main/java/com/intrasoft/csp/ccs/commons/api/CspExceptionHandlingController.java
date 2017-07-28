package com.intrasoft.csp.ccs.commons.api;

//import com.intrasoft.csp.ccs.commons.apiHttpStatusResponse.HttpStatusResponseType;
import com.intrasoft.csp.ccs.commons.exceptions.CspCommonException;
import com.intrasoft.csp.ccs.commons.exceptions.InvalidDataTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class CspExceptionHandlingController {

    /**
     * Static LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CspExceptionHandlingController.class);

    @ExceptionHandler({CspCommonException.class})
    public void handleBadRequests(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
        LOGGER.error("Bad request: {} from {}, Exception: {} {}",
                request.getRequestURI(),
                request.getRemoteHost(),
                e.getStackTrace()[0].toString(),
                e.getLocalizedMessage());

        response.sendError(HttpStatus.BAD_REQUEST.value(), e.getLocalizedMessage());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public void handleInvalidMessages(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
        LOGGER.error("Bad request: {} from {}, Exception: {} {}",
                request.getRequestURI(),
                request.getRemoteHost(),
                e.getStackTrace()[0].toString(),
                e.getLocalizedMessage());

        //response.sendError(HttpStatus.BAD_REQUEST.value(), HttpStatusResponseType.MALFORMED_INTEGRATION_DATA_STRUCTURE.getReasonPhrase());
    }

    @ExceptionHandler({InvalidDataTypeException.class})
    public void handleMissingRequiredPropsMessages(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
        LOGGER.error("Bad request: {} from {}, Exception: {} {}",
                request.getRequestURI(),
                request.getRemoteHost(),
                e.getStackTrace()[0].toString(),
                e.getLocalizedMessage());

        //response.sendError(HttpStatus.BAD_REQUEST.value(), HttpStatusResponseType.MALFORMED_INTEGRATION_DATA_STRUCTURE.getReasonPhrase()+
         //       "\n"+e.getMessage());
    }
}
