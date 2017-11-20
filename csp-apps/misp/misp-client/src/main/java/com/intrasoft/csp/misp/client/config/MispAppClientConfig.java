package com.intrasoft.csp.misp.client.config;

import com.intrasoft.csp.libraries.restclient.config.RestTemplateConfiguration;
import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;
import com.intrasoft.csp.libraries.restclient.handlers.ExceptionHandler;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.misp.client.MispAppClient;
import com.intrasoft.csp.misp.client.impl.MispAppClientImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class MispAppClientConfig {

    @Value("${misp.app.protocol}")
    private String protocol;

    @Value("${misp.app.host}")
    private String host;

    @Value("${misp.app.port}")
    private String port;

    @Value("${misp.app.authorization.key}")
    private String authorizationKey;

    @Value("${retry.backOffPeriod:5000}")
    private String backOffPeriod;

    @Value("${retry.maxAttempts:3}")
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
    private static final ConcurrentHashMap<Integer, String> AVOID_RETRY_ON_STATUS_CODE = new ConcurrentHashMap<>();

    static {
        SUPPORTED_EXCEPTIONS.put(CspBusinessException.class.getName(), CspBusinessException::new);
        AVOID_RETRY_ON_STATUS_CODE.put(HttpStatus.NOT_FOUND.value(), "Event already exists or url not found");
    }

    @Autowired
    @Qualifier("MispAppRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Bean(name="MispAppRestTemplate")
    public RetryRestTemplate getRetryRestTemplate() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        RestTemplateConfiguration restTemplateConfiguration = new RestTemplateConfiguration(backOffPeriod, maxAttempts, cspClientSslEnabled, cspClientSslJksKeystore, cspClientSslJksKeystorePassword, resourcePatternResolver);
        return restTemplateConfiguration.getRestTemplateWithOptions(SUPPORTED_EXCEPTIONS, AVOID_RETRY_ON_STATUS_CODE);
    }

    @Bean(name = "MispAppClient")
    public MispAppClient addMispEvent(){
        MispAppClient client = new MispAppClientImpl();
        client.setProtocolHostPortHeaders(protocol,host,port, authorizationKey);
        return client;
    }

}
