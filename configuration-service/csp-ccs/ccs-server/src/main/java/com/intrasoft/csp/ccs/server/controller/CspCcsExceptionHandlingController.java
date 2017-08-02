package com.intrasoft.csp.ccs.server.controller;


import com.intrasoft.csp.ccs.commons.routes.ApiContextUrl;
import com.intrasoft.csp.ccs.server.config.context.DataContextUrl;
import com.intrasoft.csp.ccs.server.config.CspCcsException;
import com.intrasoft.csp.ccs.server.config.types.HttpStatusResponseType;
import com.intrasoft.csp.ccs.server.domain.RestErrorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class CspCcsExceptionHandlingController extends ResponseEntityExceptionHandler implements ApiContextUrl, DataContextUrl {

    private static Logger LOG_EXCEPTION = LoggerFactory.getLogger("exc-log");


    @ExceptionHandler(value = {CspCcsException.class})
    protected ResponseEntity<Object> handleCspCcsException(CspCcsException ex, WebRequest request) {
        RestErrorDTO restErrorDTO = new RestErrorDTO();

        restErrorDTO.setResponseCode(ex.getCode());
        restErrorDTO.setResponseText(ex.getMessage());
        restErrorDTO.setResponseException(ex.toString());

        String logInfo = this.getUser(request) + ", " + this.getUri(request) + ": ";
        LOG_EXCEPTION.error(logInfo + ex.getMessage() + "; " + ex.toString());

        return handleExceptionInternal(ex, restErrorDTO, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    protected ResponseEntity<Object> handleGeneralException(RuntimeException ex, WebRequest request) {
        RestErrorDTO restErrorDTO = new RestErrorDTO();

        restErrorDTO.setResponseCode(HttpStatusResponseType.FAILURE.code());
        restErrorDTO.setResponseText(HttpStatusResponseType.FAILURE.text());
        restErrorDTO.setResponseException(ex.toString());

        String logInfo = this.getUser(request) + ", " + this.getUri(request) + ": ";
        LOG_EXCEPTION.error(logInfo + ex.getMessage() + "; " + ex.toString());

        return handleExceptionInternal(ex, restErrorDTO, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }




    private String getUri(WebRequest request) {
        String uri = request.toString();

        String uriParts[] = uri.split("=");
        uri = uriParts[1];
        uriParts = uri.split(";");
        uri = uriParts[0];

        String[] uriParts1 = uri.split(API_BASEURL);
        if (uriParts1.length > 1) uri = uriParts1[1];
        String[] uriParts2 = uri.split(DATA_BASEURL);
        if (uriParts2.length > 1) uri = uriParts2[1];

        return uri;
    }

    private String getUser(WebRequest request) {
        /**
         * @TODO Get user from Header after OpenAM authentication
         */

        return "system";
    }
}
