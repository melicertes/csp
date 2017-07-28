package com.intrasoft.csp.ccs.client.config;

import com.intrasoft.csp.ccs.commons.client.CommonRetryPolicy;
import com.intrasoft.csp.ccs.commons.client.RetryRestTemplate;
import com.intrasoft.csp.ccs.commons.exceptions.*;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
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

    @Value("${csp.client.ssl.enabled:false}")
    Boolean cspClientSslEnabled;

    @Value("${csp.client.ssl.jks.keystore:path}")
    String cspClientSslJksKeystore;

    @Value("${csp.client.ssl.jks.keystore.password:securedPass}")
    String cspClientSslJksKeystorePassword;

    @Autowired
    ResourcePatternResolver resourcePatternResolver;

    private static final ConcurrentHashMap<String, ExceptionHandler> SUPPORTED_EXCEPTIONS = new ConcurrentHashMap<>();

    static {
        SUPPORTED_EXCEPTIONS.put(CspGeneralException.class.getName(), CspGeneralException::new);
        SUPPORTED_EXCEPTIONS.put(InvalidDataTypeException.class.getName(), InvalidDataTypeException::new);

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
    public RetryRestTemplate getRestTemplate() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {

        //Creates the restTemplate instance
        final RetryRestTemplate retryRestTemplate = new RetryRestTemplate();
        if(cspClientSslEnabled!=null && cspClientSslEnabled) {
            retryRestTemplate.setRequestFactory(sslFactory());
        }
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

    private ClientHttpRequestFactory sslFactory() throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        String keyStoreFile = cspClientSslJksKeystore;
        String keyStorePassword = cspClientSslJksKeystorePassword;

        InputStream keystoreInputStream = resourcePatternResolver.getResource(keyStoreFile).getInputStream();

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(keystoreInputStream, keyStorePassword.toCharArray());

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
                new SSLContextBuilder()
                        .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                        .loadKeyMaterial(keyStore, keyStorePassword.toCharArray())
                        .build(),
                NoopHostnameVerifier.INSTANCE);

        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();

        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return requestFactory;
    }
}
