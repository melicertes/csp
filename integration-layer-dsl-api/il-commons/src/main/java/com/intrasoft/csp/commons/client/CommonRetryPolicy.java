package com.intrasoft.csp.commons.client;

import com.intrasoft.csp.commons.exceptions.CspBusinessException;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.SimpleRetryPolicy;

import java.util.Collections;
import java.util.Map;


public class CommonRetryPolicy extends SimpleRetryPolicy {


    private BinaryExceptionClassifier businessExceptionClassifier = new BinaryExceptionClassifier(false);

    public CommonRetryPolicy() {
        this(DEFAULT_MAX_ATTEMPTS, Collections
                .<Class<? extends Throwable>, Boolean>singletonMap(Exception.class, true));
    }

    public CommonRetryPolicy(int maxAttempts, Map<Class<? extends Throwable>, Boolean> retryableExceptions) {
        this(maxAttempts, retryableExceptions, false);
    }

    public CommonRetryPolicy(int maxAttempts, Map<Class<? extends Throwable>, Boolean> retryableExceptions, boolean traverseCauses) {
        super(maxAttempts, retryableExceptions, traverseCauses);
        this.businessExceptionClassifier = new BinaryExceptionClassifier(Collections
                .<Class<? extends Throwable>, Boolean>singletonMap(CspBusinessException.class, true));
        this.businessExceptionClassifier.setTraverseCauses(false);
    }

    public CommonRetryPolicy(int maxAttempts, Map<Class<? extends Throwable>, Boolean> retryableExceptions, Map<Class<? extends Throwable>, Boolean> businessExceptions) {
        super(maxAttempts, retryableExceptions);
        this.businessExceptionClassifier = new BinaryExceptionClassifier(businessExceptions, true);
        this.businessExceptionClassifier.setTraverseCauses(false);
    }


    @Override
    public boolean canRetry(RetryContext context) {
        final Throwable t = context.getLastThrowable();
        return (t == null) || !(businessExceptionClassifier.classify(t)) && super.canRetry(context);
    }
}
