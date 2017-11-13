package com.intrasoft.csp.client.config;

import com.intrasoft.csp.client.ElasticClient;
import com.intrasoft.csp.client.impl.ElasticClientImpl;
import com.intrasoft.csp.libraries.restclient.config.RestTemplateConfiguration;
import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
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
public class ElasticClientConfig {

    @Value("${elastic.protocol}")
    String elasticProtocol;
    @Value("${elastic.host}")
    String elasticHost;
    @Value("${elastic.port}")
    String elasticPort;
    @Value("${elastic.path}")
    String elasticPath;

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

    private static final ConcurrentHashMap<String, com.intrasoft.csp.libraries.restclient.handlers.ExceptionHandler> SUPPORTED_EXCEPTIONS = new ConcurrentHashMap<>();

    static {
        SUPPORTED_EXCEPTIONS.put(CspBusinessException.class.getName(), CspBusinessException::new);
    }

    @Bean(name = "elasticClient")
    public ElasticClient getElasticClient(){
        ElasticClient elasticClient = new ElasticClientImpl();
        elasticClient.setProtocolHostPort(elasticProtocol,elasticHost,elasticPort);
        return elasticClient;
    }

    @Autowired
    @Qualifier("ElasticRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Bean(name="ElasticRestTemplate")
    public RetryRestTemplate getRetryRestTemplate() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        RestTemplateConfiguration restTemplateConfiguration = new RestTemplateConfiguration(backOffPeriod, maxAttempts, cspClientSslEnabled, cspClientSslJksKeystore, cspClientSslJksKeystorePassword, resourcePatternResolver);
        return restTemplateConfiguration.getRestTemplateWithSupportedExceptions(SUPPORTED_EXCEPTIONS);
    }
}
