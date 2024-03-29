package com.intrasoft.csp.libraries.restclient.config;

import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;
import com.intrasoft.csp.libraries.restclient.handlers.CommonExceptionHandler;
import com.intrasoft.csp.libraries.restclient.handlers.ExceptionHandler;
import com.intrasoft.csp.libraries.restclient.service.CommonRetryPolicy;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
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
import org.springframework.web.client.RestClientException;


import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class RestTemplateConfiguration {

    private String backOffPeriod="5000";
    private String maxAttempts="3";
    Boolean cspClientSslEnabled=false;
    String cspClientSslJksKeystore="<tbd>";
    String cspClientSslJksKeystorePassword="<tbd>";
    ResourcePatternResolver resourcePatternResolver;


    private static final ConcurrentHashMap<String, ExceptionHandler> SUPPORTED_EXCEPTIONS = new ConcurrentHashMap<>();

    static {
        SUPPORTED_EXCEPTIONS.put(CspBusinessException.class.getName(), CspBusinessException::new);
    }

    public RestTemplateConfiguration(String backOffPeriod, String maxAttempts, ResourcePatternResolver resourcePatternResolver) {
        this.backOffPeriod = backOffPeriod;
        this.maxAttempts = maxAttempts;
        this.resourcePatternResolver = resourcePatternResolver;
    }

    public RestTemplateConfiguration(String backOffPeriod, String maxAttempts, Boolean cspClientSslEnabled, String cspClientSslJksKeystore, String cspClientSslJksKeystorePassword, ResourcePatternResolver resourcePatternResolver) {
        this.backOffPeriod = backOffPeriod;
        this.maxAttempts = maxAttempts;
        this.cspClientSslEnabled = cspClientSslEnabled;
        this.cspClientSslJksKeystore = cspClientSslJksKeystore;
        this.cspClientSslJksKeystorePassword = cspClientSslJksKeystorePassword;
        this.resourcePatternResolver = resourcePatternResolver;
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


    public RetryRestTemplate getRestTemplate() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        return getRestTemplateWithSupportedExceptions(SUPPORTED_EXCEPTIONS);
    }

    public RetryRestTemplate getRestTemplateWithSupportedExceptions(ConcurrentHashMap<String, ExceptionHandler> supportedExceptionsMap) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        return getRestTemplateWithOptions(supportedExceptionsMap,null);
    }

    public RetryRestTemplate getRestTemplateWithOptions(ConcurrentHashMap<String, ExceptionHandler> supportedExceptionsMap,ConcurrentHashMap<Integer, String> avoidRetryOnStatusCodeMap) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        //Creates the restTemplate instance
        final RetryRestTemplate retryRestTemplate = new RetryRestTemplate();
        if(cspClientSslEnabled!=null && cspClientSslEnabled) {
            retryRestTemplate.setRequestFactory(sslFactory());
        }
        //Create Custom Exception Handler
        final CommonExceptionHandler exceptionHandler = new CommonExceptionHandler();

        if(avoidRetryOnStatusCodeMap!=null){
            exceptionHandler.setAvoidRetryOnStatusCodeMap(avoidRetryOnStatusCodeMap);
        }

        //Set Supported Exceptions
        exceptionHandler.setSupportedExceptions(supportedExceptionsMap);

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

    public String getBackOffPeriod() {
        return backOffPeriod;
    }

    public void setBackOffPeriod(String backOffPeriod) {
        this.backOffPeriod = backOffPeriod;
    }

    public String getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(String maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public Boolean getCspClientSslEnabled() {
        return cspClientSslEnabled;
    }

    public void setCspClientSslEnabled(Boolean cspClientSslEnabled) {
        this.cspClientSslEnabled = cspClientSslEnabled;
    }

    public String getCspClientSslJksKeystore() {
        return cspClientSslJksKeystore;
    }

    public void setCspClientSslJksKeystore(String cspClientSslJksKeystore) {
        this.cspClientSslJksKeystore = cspClientSslJksKeystore;
    }

    public String getCspClientSslJksKeystorePassword() {
        return cspClientSslJksKeystorePassword;
    }

    public void setCspClientSslJksKeystorePassword(String cspClientSslJksKeystorePassword) {
        this.cspClientSslJksKeystorePassword = cspClientSslJksKeystorePassword;
    }

    public ResourcePatternResolver getResourcePatternResolver() {
        return resourcePatternResolver;
    }

    public void setResourcePatternResolver(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }
}
