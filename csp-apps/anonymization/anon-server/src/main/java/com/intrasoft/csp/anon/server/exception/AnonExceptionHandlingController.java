package com.intrasoft.csp.anon.server.exception;


import com.intrasoft.csp.anon.commons.exceptions.AnonException;
import com.intrasoft.csp.anon.commons.exceptions.MappingNotFoundForGivenTupleException;
import com.intrasoft.csp.anon.commons.exceptions.InvalidDataTypeException;
import com.intrasoft.csp.commons.apiHttpStatusResponse.HttpStatusResponseType;
import com.intrasoft.csp.libraries.restclient.controller.RestExceptionHandlingController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class AnonExceptionHandlingController extends RestExceptionHandlingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnonExceptionHandlingController.class);

    /**
     * Example to override default internal exception handling
    @ExceptionHandler(value = {MappingNotFoundForGivenTupleException.class})
    public ResponseEntity<Object>  invalidCspEntryException(WebRequest request, HttpServletRequest req, HttpServletResponse response, Exception e) throws IOException {
        return handleException(e,HttpStatusResponseType.MAPPING_NOT_FOUND_FOR_GIVEN_TUPLE.value(),HttpStatus.BAD_REQUEST, request);
    }*/

    @ExceptionHandler({MappingNotFoundForGivenTupleException.class})
    public void handleBadRequests(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
        LOGGER.error("Bad request: {} from {}, Exception: {} {}",
                request.getRequestURI(),
                request.getRemoteHost(),
                e.getStackTrace()[0].toString(),
                e.getLocalizedMessage());

        // if sending an http code other than 4xx the internal handling exception will not be triggered
        // 401-unauthorized will also NOT trigger the internal handling exception mechanism
        response.sendError(HttpStatusResponseType.MAPPING_NOT_FOUND_FOR_GIVEN_TUPLE.value(), e.getLocalizedMessage());
    }

    @ExceptionHandler(value = {InvalidDataTypeException.class})
    public ResponseEntity<Object>  unsupportedDataType(WebRequest request, HttpServletRequest req, HttpServletResponse response, Exception e) throws IOException {
        return handleExceptionInternal(e,
                super.getRestErrorDTO(e, HttpStatusResponseType.UNSUPPORTED_DATA_TYPE.value(),req.getRequestURI()),
                new HttpHeaders(), HttpStatus.UNSUPPORTED_MEDIA_TYPE, request);
    }

//    public ResponseEntity<Object>  mappingNotFoundForGivenTupleException(WebRequest request, HttpServletRequest req, HttpServletResponse response, Exception e) throws IOException {
//        return handleExceptionInternal(e,
//                super.getRestErrorDTO(e, HttpStatusResponseType.MAPPING_NOT_FOUND_FOR_GIVEN_TUPLE.value(),req.getRequestURI()),
//                new HttpHeaders(), HttpStatus.OK, request);
//    }

    @ExceptionHandler(value = {AnonException.class})
    public ResponseEntity<Object>  malformedIntegrationDataStructure(WebRequest request, HttpServletRequest req, HttpServletResponse response, Exception e) throws IOException {
        return handleExceptionInternal(e,
                super.getRestErrorDTO(e, HttpStatusResponseType.MALFORMED_INTEGRATION_DATA_STRUCTURE.value(),req.getRequestURI()),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);

}
}