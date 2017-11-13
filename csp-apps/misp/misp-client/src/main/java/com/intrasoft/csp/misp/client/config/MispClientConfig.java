package com.intrasoft.csp.misp.client.config;

import com.intrasoft.csp.libraries.restclient.config.RestTemplateConfiguration;
import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;
import com.intrasoft.csp.libraries.restclient.handlers.ExceptionHandler;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.libraries.versioning.client.ApiVersionClient;
import com.intrasoft.csp.libraries.versioning.client.ApiVersionClientImpl;
import com.intrasoft.csp.misp.client.MispClient;
import com.intrasoft.csp.misp.client.impl.MispClientImpl;
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

import static com.intrasoft.csp.misp.commons.config.ApiContextUrl.REST_API_V1;

@Configuration
public class MispClientConfig {

    @Value("${adapter.server.protocol}")
    private String protocol;

    @Value("${adapter.server.host}")
    private String host;

    @Value("${adapter.server.port}")
    private String port;

    @Value("${retry.backOffPeriod:5000}")
    private String backOffPeriod;

    @Value("${retry.maxAttempts:3}")
    private String maxAttempts;

    @Value("${client.ssl.enabled:false}")
    Boolean cspClientSslEnabled;

    @Value("${client.ssl.jks.keystore:path}")
    String cspClientSslJksKeystore;

    @Value("${client.ssl.jks.keystore.password:securedPass}")
    String cspClientSslJksKeystorePassword;

    @Autowired
    ResourcePatternResolver resourcePatternResolver;

    private static final ConcurrentHashMap<String, ExceptionHandler> SUPPORTED_EXCEPTIONS = new ConcurrentHashMap<>();

    static {
        SUPPORTED_EXCEPTIONS.put(CspBusinessException.class.getName(), CspBusinessException::new);
    }

    @Autowired
    @Qualifier("MispRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Bean(name = "MispClient")
    public MispClient MispClient(){
        return new MispClientImpl();
    }

    @Bean(name="MispRestTemplate")
    public RetryRestTemplate getRetryRestTemplate() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        RestTemplateConfiguration restTemplateConfiguration = new RestTemplateConfiguration(backOffPeriod, maxAttempts, cspClientSslEnabled, cspClientSslJksKeystore, cspClientSslJksKeystorePassword, resourcePatternResolver);
        return restTemplateConfiguration.getRestTemplateWithSupportedExceptions(SUPPORTED_EXCEPTIONS);
    }

    @Bean(name = "MispApiVersionClient")
    public ApiVersionClient getApiVersionClient() throws Exception {
        return new ApiVersionClientImpl(protocol, host, port, REST_API_V1, retryRestTemplate);
    }
}
