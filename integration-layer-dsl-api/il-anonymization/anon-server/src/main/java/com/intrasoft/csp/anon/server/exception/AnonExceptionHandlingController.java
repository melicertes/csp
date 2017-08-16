package com.intrasoft.csp.anon.server.exception;


import com.intrasoft.csp.anon.commons.exceptions.MalformedIntegrationDataStructureException;
import com.intrasoft.csp.anon.commons.exceptions.MappingNotFoundForGivenTupleException;
import com.intrasoft.csp.anon.commons.exceptions.UnsupportedDataTypeException;
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

    @ExceptionHandler(value = {MappingNotFoundForGivenTupleException.class})
    public ResponseEntity<Object>  mappingNotFoundForGivenTupleException(WebRequest request, HttpServletRequest req, HttpServletResponse response, Exception e) throws IOException {
        return handleExceptionInternal(e,
                super.getRestErrorDTO(e, HttpStatusResponseType.MAPPING_NOT_FOUND_FOR_GIVEN_TUPLE.value(),req.getRequestURI()),
                new HttpHeaders(), HttpStatus.OK, request);
    }

    @ExceptionHandler(value = {MalformedIntegrationDataStructureException.class})
    public ResponseEntity<Object>  malformedIntegrationDataStructure(WebRequest request, HttpServletRequest req, HttpServletResponse response, Exception e) throws IOException {
        return handleExceptionInternal(e,
                super.getRestErrorDTO(e, HttpStatusResponseType.MALFORMED_INTEGRATION_DATA_STRUCTURE.value(),req.getRequestURI()),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {UnsupportedDataTypeException.class})
    public ResponseEntity<Object>  unsupportedDataType(WebRequest request, HttpServletRequest req, HttpServletResponse response, Exception e) throws IOException {
        return handleExceptionInternal(e,
                super.getRestErrorDTO(e, HttpStatusResponseType.UNSUPPORTED_DATA_TYPE.value(),req.getRequestURI()),
                new HttpHeaders(), HttpStatus.UNSUPPORTED_MEDIA_TYPE, request);
    }

}
