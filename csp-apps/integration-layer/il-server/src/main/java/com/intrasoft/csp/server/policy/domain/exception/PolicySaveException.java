package com.intrasoft.csp.server.policy.domain.exception;

import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;

public class PolicySaveException extends CspBusinessException{
    private static final long serialVersionUID = -8544626230754124285L;

    public PolicySaveException(String message) {
        super(message);
    }

    public PolicySaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
