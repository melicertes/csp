package com.intrasoft.csp.commons.exceptions;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.commons.model.RestErrorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RestTemplate Custom Exception Handler.
 */
public class CommonExceptionHandler implements ResponseErrorHandler {

    private Logger LOGGER = LoggerFactory
            .getLogger(CommonExceptionHandler.class);

    /**
     * Json factory instance.
     */
    private final JsonFactory factory = new JsonFactory();

    /**
     * Object Mapper used to covert JSON object to Java object.
     */
    private final ObjectMapper objectMapper = new ObjectMapper(factory);

    /**
     * HashMap which holds the supported exception classes.
     */
    private final ConcurrentHashMap<String, ExceptionHandler> exceptionClasses = new ConcurrentHashMap<>();

    public CommonExceptionHandler() {
        exceptionClasses.put(CspBusinessException.class.getName(), CspBusinessException::new);
    }

    /**
     * Delegates to {@link #hasError(HttpStatus)} with the response status code.
     */
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return hasError(getHttpStatusCode(response));
    }

    private HttpStatus getHttpStatusCode(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode;
        try {
            statusCode = response.getStatusCode();
        } catch (IllegalArgumentException ex) {
            throw new UnknownHttpStatusCodeException(response.getRawStatusCode(),
                    response.getStatusText(), response.getHeaders(), getResponseBody(response), getCharset(response));
        }
        return statusCode;
    }

    /**
     * Template method called from {@link #hasError(ClientHttpResponse)}.
     * <p>The default implementation checks if the given status code is
     * {@link org.springframework.http.HttpStatus.Series#CLIENT_ERROR CLIENT_ERROR}
     * or {@link org.springframework.http.HttpStatus.Series#SERVER_ERROR SERVER_ERROR}.
     * Can be overridden in subclasses.
     *
     * @param statusCode the HTTP status code
     * @return {@code true} if the response has an error; {@code false} otherwise
     */
    protected boolean hasError(HttpStatus statusCode) {
        return (statusCode.series() == HttpStatus.Series.CLIENT_ERROR ||
                statusCode.series() == HttpStatus.Series.SERVER_ERROR);
    }


    /**
     * This default implementation throws a {@link HttpClientErrorException} if the response status code
     * is {@link org.springframework.http.HttpStatus.Series#CLIENT_ERROR}, a {@link HttpServerErrorException}
     * if it is {@link org.springframework.http.HttpStatus.Series#SERVER_ERROR},
     * and a {@link RestClientException} in other cases.
     */
    @Override
    public void handleError(ClientHttpResponse response) throws IOException, RestClientException {
        HttpStatus statusCode = getHttpStatusCode(response);
        switch (statusCode.series()) {
            case CLIENT_ERROR:
                final byte[] responseBody = getResponseBody(response);
                final Charset charset = getCharset(response);
                final String statusText = response.getStatusText();
                final HttpHeaders httpHeaders = response.getHeaders();
                final RestErrorDTO errorDTO;
                
                try {
                    errorDTO = objectMapper.readValue(new String(responseBody, charset), RestErrorDTO.class);
                    LOGGER.error("Exception: " + errorDTO.toString());
                } catch (final Exception e) {
                    //Wasn't able to map String on ErrorDTO.
                    //It is an Unknown Exception
                    //Throw Default Exception
                    final HttpClientErrorException clientErrorException = new HttpClientErrorException(statusCode, statusText, httpHeaders, responseBody, charset);
                    LOGGER.error("Unknown Exception: " + clientErrorException.getMessage());
                    throw clientErrorException;
                }

                if (exceptionClasses.containsKey(errorDTO.getException())) {
                    throw (exceptionClasses.get(errorDTO.getException())).create(errorDTO.getMessage());
                } else {
                    throw new HttpClientErrorException(statusCode, statusText, httpHeaders, responseBody, charset);
                }

            case SERVER_ERROR:
                throw new HttpServerErrorException(statusCode, response.getStatusText(),
                        response.getHeaders(), getResponseBody(response), getCharset(response));
            default:
                throw new RestClientException("Unknown status code [" + statusCode + "]");
        }
    }

    private byte[] getResponseBody(ClientHttpResponse response) {
        try {
            InputStream responseBody = response.getBody();
            if (responseBody != null) {
                return FileCopyUtils.copyToByteArray(responseBody);
            }
        } catch (IOException ex) {
            // ignore
        }
        return new byte[0];
    }

    private Charset getCharset(ClientHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        MediaType contentType = headers.getContentType();
        return contentType != null ? contentType.getCharSet() : null;
    }


    public void setSupportedExceptions(final String key, final ExceptionHandler value) {
        exceptionClasses.put(key, value);
    }

    public void setSupportedExceptions(final ConcurrentHashMap<String, ExceptionHandler> exceptionHandlers) {
        exceptionClasses.putAll(exceptionHandlers);
    }

}
