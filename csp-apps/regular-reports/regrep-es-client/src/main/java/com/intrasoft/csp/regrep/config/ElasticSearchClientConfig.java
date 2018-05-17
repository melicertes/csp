package com.intrasoft.csp.regrep.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.libraries.restclient.config.RestTemplateConfiguration;
import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;
import com.intrasoft.csp.libraries.restclient.handlers.ExceptionHandler;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.regrep.ElasticSearchClient;
import com.intrasoft.csp.regrep.impl.ElasticSearchClientImpl;
import com.intrasoft.csp.regrep.routes.ContextUrl;
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
public class ElasticSearchClientConfig implements ContextUrl {

    @Value("${app.es.protocol}")
    private String protocol;
    @Value("${app.es.host}")
    private String host;
    @Value("${app.es.port}")
    private String port;

    @Value("${app.es.csp.retry.backOffPeriod:5000}")
    private String backOffPeriod;

    @Value("${app.es.csp.retry.maxAttempts:3}")
    private String maxAttempts;

    @Value("${app.es.client.ssl.enabled:false}")
    Boolean cspClientSslEnabled;

    @Value("${app.es.client.ssl.jks.keystore:path}")
    String cspClientSslJksKeystore;

    @Value("${app.es.client.ssl.jks.keystore.password:securedPass}")
    String cspClientSslJksKeystorePassword;

    private static final ConcurrentHashMap<String, ExceptionHandler> SUPPORTED_EXCEPTIONS = new ConcurrentHashMap<>();

    static {
        SUPPORTED_EXCEPTIONS.put(CspBusinessException.class.getName(), CspBusinessException::new);
    }

    @Autowired
    ResourcePatternResolver resourcePatternResolver;

    @Bean(name = "elasticSearchClient")
    public ElasticSearchClient getElasticSearchClient(){
        ElasticSearchClient elasticSearchClient = new ElasticSearchClientImpl(getElasticSearchBaseContext());
        elasticSearchClient.setProtocolHostPort(protocol,host,port);
        return elasticSearchClient;
    }

/*
    @Bean(name = "elasticSearchClientRestTemplate")
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
*/

    @Autowired
    @Qualifier("ElasticRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Bean(name="ElasticRestTemplate")
    public RetryRestTemplate getRetryRestTemplate() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        RestTemplateConfiguration restTemplateConfiguration = new RestTemplateConfiguration(backOffPeriod, maxAttempts, cspClientSslEnabled, cspClientSslJksKeystore, cspClientSslJksKeystorePassword, resourcePatternResolver);
        return restTemplateConfiguration.getRestTemplateWithSupportedExceptions(SUPPORTED_EXCEPTIONS);
    }

    @Bean(name = "elasticSearchClientObjectMapper")
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }


    public String getElasticSearchBaseContext(){
        return protocol + "://" + host + ":" + port;
    }

}
