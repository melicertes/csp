package com.intrasoft.csp.misp.config;

import com.intrasoft.csp.client.CspClient;
import com.intrasoft.csp.client.impl.CspClientImpl;
import com.intrasoft.csp.commons.exceptions.InvalidDataTypeException;
import com.intrasoft.csp.commons.routes.ContextUrl;
import com.intrasoft.csp.libraries.restclient.config.RestTemplateConfiguration;
import com.intrasoft.csp.libraries.restclient.exceptions.CspGeneralException;
import com.intrasoft.csp.libraries.restclient.handlers.ExceptionHandler;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.libraries.versioning.client.ApiVersionClient;
import com.intrasoft.csp.libraries.versioning.client.ApiVersionClientImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by iskitsas on 5/3/17.
 */
@Configuration
public class CspClientConfig implements ContextUrl {
    @Value("${csp.server.protocol:http}")
    private String protocol;

    @Value("${csp.server.host:localhost}")
    private String host;

    @Value("${csp.server.port:8081}")
    private String port;

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

    @Bean(name = "CspClient")
    public CspClient cspClient(){
        return new CspClientImpl();
    }

    @Autowired
    @Qualifier("CspRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Bean(name="CspRestTemplate")
    public RetryRestTemplate getRetryRestTemplate() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        RestTemplateConfiguration restTemplateConfiguration = new RestTemplateConfiguration(backOffPeriod, maxAttempts, cspClientSslEnabled, cspClientSslJksKeystore, cspClientSslJksKeystorePassword, resourcePatternResolver);
        return restTemplateConfiguration.getRestTemplateWithSupportedExceptions(SUPPORTED_EXCEPTIONS);
    }

    @Bean(name = "CspApiVersionClient")
    public ApiVersionClient getApiVersionClient() throws Exception {
        return new ApiVersionClientImpl(protocol, host, port, REST_API_V1, retryRestTemplate);
    }
}
