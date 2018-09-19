package com.fraunhofer.csp.rt.config;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.fraunhofer.csp.rt.client.RtClient;
import com.fraunhofer.csp.rt.client.impl.RtClientImpl;
import com.intrasoft.csp.libraries.restclient.config.RestTemplateConfiguration;
import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;

/**
 * Created by Majid Salehi on 4/8/17.
 */
@Configuration
public class RtClientConfig {

	@Value("${rt.app.protocol}")
	String rtProtocol;
	@Value("${rt.app.username}")
	String username;

	@Value("${rt.host}")
	String rtHost;
	@Value("${rt.port}")
	String rtPort;

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

	private static final ConcurrentHashMap<String, com.intrasoft.csp.libraries.restclient.handlers.ExceptionHandler> SUPPORTED_EXCEPTIONS = new ConcurrentHashMap<>();

	static {
		SUPPORTED_EXCEPTIONS.put(CspBusinessException.class.getName(), CspBusinessException::new);
	}

	@Bean(name = "RtClient")
	public RtClient getRtClient() {
		RtClient rtClient = new RtClientImpl();
		rtClient.setProtocolHostPort(rtProtocol, rtHost, rtPort, username);
		return rtClient;
	}

	@Autowired
	@Qualifier("RtRestTemplate")
	RetryRestTemplate retryRestTemplate;

	@Bean(name = "RtRestTemplate")
	public RetryRestTemplate getRetryRestTemplate() throws CertificateException, UnrecoverableKeyException,
			NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
		RestTemplateConfiguration restTemplateConfiguration = new RestTemplateConfiguration(backOffPeriod, maxAttempts,
				cspClientSslEnabled, cspClientSslJksKeystore, cspClientSslJksKeystorePassword, resourcePatternResolver);
		return restTemplateConfiguration.getRestTemplateWithSupportedExceptions(SUPPORTED_EXCEPTIONS);
	}
}
