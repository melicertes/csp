package com.intrasoft.csp.commons.exceptions.anon;

import com.intrasoft.csp.commons.exceptions.CspBusinessException;
import org.springframework.web.client.RestClientException;

public class AnonException extends RestClientException {

    public AnonException(String message) {
        super(message);
    }

    public AnonException(String message, Throwable cause) {
        super(message, cause);
    }
}
