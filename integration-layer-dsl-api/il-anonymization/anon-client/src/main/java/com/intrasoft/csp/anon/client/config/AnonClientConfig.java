package com.intrasoft.csp.anon.client.config;


import com.intrasoft.csp.anon.client.AnonClient;
import com.intrasoft.csp.anon.client.impl.AnonClientImpl;
import com.intrasoft.csp.anon.commons.exceptions.AnonException;
import com.intrasoft.csp.anon.commons.exceptions.UnsupportedDataTypeException;
import com.intrasoft.csp.anon.commons.exceptions.MappingNotFoundForGivenTupleException;
import com.intrasoft.csp.anon.commons.model.AnonContextUrl;
import com.intrasoft.csp.libraries.restclient.config.RestTemplateConfiguration;
import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;
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
 * Created by chris on 14/7/2017.
 */
@Configuration
public class AnonClientConfig implements AnonContextUrl {
    @Value("${anon.server.protocol:http}")
    private String protocol;

    @Value("${anon.server.host:localhost}")
    private String host;

    @Value("${anon.server.port:8085}")
    private String port;

    @Value("${anon.retry.backOffPeriod:5000}")
    private String backOffPeriod;

    @Value("${anon.retry.maxAttempts:3}")
    private String maxAttempts;

    @Value("${anon.client.ssl.enabled:false}")
    Boolean cspClientSslEnabled;

    @Value("${anon.client.ssl.jks.keystore:path}")
    String cspClientSslJksKeystore;

    @Value("${anon.client.ssl.jks.keystore.password:securedPass}")
    String cspClientSslJksKeystorePassword;

    @Autowired
    ResourcePatternResolver resourcePatternResolver;

    private static final ConcurrentHashMap<String, ExceptionHandler> SUPPORTED_EXCEPTIONS = new ConcurrentHashMap<>();

    static {
        SUPPORTED_EXCEPTIONS.put(CspBusinessException.class.getName(), CspBusinessException::new);
        SUPPORTED_EXCEPTIONS.put(UnsupportedDataTypeException.class.getName(), UnsupportedDataTypeException::new);
        SUPPORTED_EXCEPTIONS.put(AnonException.class.getName(), AnonException::new);
        SUPPORTED_EXCEPTIONS.put(MappingNotFoundForGivenTupleException.class.getName(), MappingNotFoundForGivenTupleException::new);
    }

    @Bean(name = "anonClient")
    public AnonClient anonClient(){
        return new AnonClientImpl();
    }

    @Autowired
    @Qualifier("AnonRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Bean(name="AnonRestTemplate")
    public RetryRestTemplate getRetryRestTemplate() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        RestTemplateConfiguration restTemplateConfiguration = new RestTemplateConfiguration(backOffPeriod, maxAttempts, cspClientSslEnabled, cspClientSslJksKeystore, cspClientSslJksKeystorePassword, resourcePatternResolver);
        return restTemplateConfiguration.getRestTemplateWithSupportedExceptions(SUPPORTED_EXCEPTIONS);
    }

    @Bean(name = "AnonApiVersionClient")
    public ApiVersionClient getApiVersionClient() throws Exception {
        return new ApiVersionClientImpl(protocol, host, port, REST_API_V1, retryRestTemplate);
    }

}
