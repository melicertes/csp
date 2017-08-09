package com.intrasoft.csp.conf.client.config;


import com.intrasoft.csp.commons.exceptions.InvalidDataTypeException;
import com.intrasoft.csp.conf.client.ConfClient;
import com.intrasoft.csp.conf.client.impl.ConfClientImpl;
import com.intrasoft.csp.conf.commons.context.ApiContextUrl;

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

@Configuration
public class ConfClientConfig implements ApiContextUrl {
    @Value("${conf.server.protocol:http}")
    private String protocol;

    @Value("${conf.server.host:localhost}")
    private String host;

    @Value("${conf.server.port:8090}")
    private String port;

    @Value("${conf.retry.backOffPeriod:5000}")
    private String backOffPeriod;

    @Value("${conf.retry.maxAttempts:3}")
    private String maxAttempts;

    @Value("${conf.client.ssl.enabled:false}")
    Boolean cspClientSslEnabled;

    @Value("${conf.client.ssl.jks.keystore:path}")
    String cspClientSslJksKeystore;

    @Value("${conf.client.ssl.jks.keystore.password:securedPass}")
    String cspClientSslJksKeystorePassword;

    @Autowired
    ResourcePatternResolver resourcePatternResolver;

    private static final ConcurrentHashMap<String, ExceptionHandler> SUPPORTED_EXCEPTIONS = new ConcurrentHashMap<>();

    static {
        SUPPORTED_EXCEPTIONS.put(CspBusinessException.class.getName(), CspBusinessException::new);
        SUPPORTED_EXCEPTIONS.put(InvalidDataTypeException.class.getName(), InvalidDataTypeException::new);
    }

    @Bean(name = "confClient")
    public ConfClient confClient(){
        return new ConfClientImpl();
    }

    @Autowired
    @Qualifier("ConfRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Bean(name="ConfRestTemplate")
    public RetryRestTemplate getRetryRestTemplate() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        RestTemplateConfiguration restTemplateConfiguration = new RestTemplateConfiguration(backOffPeriod, maxAttempts, cspClientSslEnabled, cspClientSslJksKeystore, cspClientSslJksKeystorePassword, resourcePatternResolver);
        return restTemplateConfiguration.getRestTemplateWithSupportedExceptions(SUPPORTED_EXCEPTIONS);
    }

    @Bean(name = "ConfApiVersionClient")
    public ApiVersionClient getApiVersionClient() throws Exception {
        return new ApiVersionClientImpl(protocol, host, port, REST_API_V1, retryRestTemplate);
    }

}
