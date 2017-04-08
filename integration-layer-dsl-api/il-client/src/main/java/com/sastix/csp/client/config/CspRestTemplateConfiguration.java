package com.sastix.csp.client.config;

import com.sastix.csp.commons.client.CommonRetryPolicy;
import com.sastix.csp.commons.client.RetryRestTemplate;
import com.sastix.csp.commons.exceptions.CommonExceptionHandler;
import com.sastix.csp.commons.exceptions.CspGeneralException;
import com.sastix.csp.commons.exceptions.ExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by iskitsas on 4/4/17.
 */
@Configuration
public class CspRestTemplateConfiguration {
    @Value("${csp.retry.backOffPeriod:5000}")
    private String backOffPeriod;

    @Value("${csp.retry.maxAttempts:3}")
    private String maxAttempts;

    private static final ConcurrentHashMap<String, ExceptionHandler> SUPPORTED_EXCEPTIONS = new ConcurrentHashMap<>();

    static {
        SUPPORTED_EXCEPTIONS.put(CspGeneralException.class.getName(), CspGeneralException::new);

    }

    /**
     * Configure and return the retry template.
     */
    public RetryTemplate getRetryTemplate() {
        //Create RetryTemplate
        final RetryTemplate retryTemplate = new RetryTemplate();

        //Create Fixed back policy
        final FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();

        //Set backOffPeriod
        fixedBackOffPolicy.setBackOffPeriod(Long.valueOf(backOffPeriod));

        //Set the backoff policy
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        //Create Simple Retry Policy
        final CommonRetryPolicy retryPolicy = new CommonRetryPolicy(Integer.valueOf(maxAttempts), Collections
                .<Class<? extends Throwable>, Boolean>singletonMap(RestClientException.class, true), false);


        //Set retry policy
        retryTemplate.setRetryPolicy(retryPolicy);

        //Return the RetryTemplate
        return retryTemplate;
    }

    /**
     * Configure and return the Rest Template.
     */
    @Bean(name = "CspRestTemplate")
    public RetryRestTemplate getRestTemplate() {

        //Creates the restTemplate instance
        final RetryRestTemplate retryRestTemplate = new RetryRestTemplate();

        //Create Custom Exception Handler
        final CommonExceptionHandler exceptionHandler = new CommonExceptionHandler();

        //Set Supported Exceptions
        exceptionHandler.setSupportedExceptions(SUPPORTED_EXCEPTIONS);

        //Set the custom exception handler ar default
        retryRestTemplate.setErrorHandler(exceptionHandler);

        //Set Retry Template
        retryRestTemplate.setRetryTemplate(getRetryTemplate());

        //Return the template instance
        return retryRestTemplate;
    }
}
