package com.intrasoft.csp.vcb.admin.config;

import com.intrasoft.csp.client.TrustCirclesClient;
import com.intrasoft.csp.client.impl.TrustCirclesClientImpl;
import com.intrasoft.csp.commons.routes.ContextUrl;
import com.intrasoft.csp.libraries.restclient.config.RestTemplateConfiguration;
import com.intrasoft.csp.libraries.restclient.exceptions.CspGeneralException;
import com.intrasoft.csp.libraries.restclient.handlers.ExceptionHandler;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
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

/**
 * Created by iskitsas on 4/8/17.
 */
@Configuration
public class TrustCirclesClientConfig implements ContextUrl {
    @Value("${app.tc.protocol:http}")
    private String protocol;

    @Value("${app.tc.host:localhost}")
    private String host;

    @Value("${app.tc.port:9000}")
    private String port;

    @Value("${app.tc.path.circles}")
    String tcPathCircles;

    @Value("${app.tc.path.teams}")
    String tcPathTeams;

    @Value("${app.tc.path.localcircle}")
    String tcPathLocalCircle;

    @Value("${app.tc.path.contacts}")
    String tcPathContacts;

    @Value("${app.tc.path.teamcontacts}")
    String tcPathTeamContacts;

    @Value("${app.tc.path.personcontacts}")
    String tcPathPersonContacts;

    @Value("${app.tc.retry.backOffPeriod:5000}")
    private String backOffPeriod;

    @Value("${app.tc.retry.maxAttempts:3}")
    private String maxAttempts;

    @Value("${app.tc.client.ssl.enabled:false}")
    Boolean tcClientSslEnabled;

    @Value("${app.tc.client.ssl.jks.keystore:path}")
    String tcClientSslJksKeystore;

    @Value("${app.tc.client.ssl.jks.keystore.password:securedPass}")
    String tcClientSslJksKeystorePassword;

    @Autowired
    ResourcePatternResolver resourcePatternResolver;

    private static final ConcurrentHashMap<String, ExceptionHandler> SUPPORTED_EXCEPTIONS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, String> AVOID_RETRY_ON_STATUS_CODE = new ConcurrentHashMap<>();

    static {
        SUPPORTED_EXCEPTIONS.put(CspGeneralException.class.getName(), CspGeneralException::new);
        AVOID_RETRY_ON_STATUS_CODE.put(HttpStatus.NOT_FOUND.value(), "TC server responded with 404");
    }

    @Bean(name = "TcClient")
    public TrustCirclesClient tcClient(){
        return new TrustCirclesClientImpl(getTcBaseContext(),getTcPathCircles(),getTcPathTeams(), getTcPathLocalCircle(), getTcPathContacts(), getTcPathTeamContacts(), getTcPathPersonContacts());
    }

    @Autowired
    @Qualifier("TcRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Bean(name="TcRestTemplate")
    public RetryRestTemplate getRetryRestTemplate() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        RestTemplateConfiguration restTemplateConfiguration = new RestTemplateConfiguration(backOffPeriod, maxAttempts,
                tcClientSslEnabled, tcClientSslJksKeystore, tcClientSslJksKeystorePassword, resourcePatternResolver);
        return restTemplateConfiguration.getRestTemplateWithOptions(SUPPORTED_EXCEPTIONS,AVOID_RETRY_ON_STATUS_CODE);
    }

    public String getTcPathCircles() {
        return tcPathCircles;
    }

    public void setTcPathCircles(String tcPathCircles) {
        this.tcPathCircles = tcPathCircles;
    }

    public String getTcPathTeams() {
        return tcPathTeams;
    }

    public void setTcPathTeams(String tcPathTeams) {
        this.tcPathTeams = tcPathTeams;
    }

    public String getTcPathLocalCircle() {
        return tcPathLocalCircle;
    }

    public void setTcPathLocalCircle(String tcPathLocalCircle) {
        this.tcPathLocalCircle = tcPathLocalCircle;
    }

    public String getTcPathContacts() {
        return tcPathContacts;
    }

    public void setTcPathContacts(String tcPathContacts) {
        this.tcPathContacts = tcPathContacts;
    }

    public String getTcPathTeamContacts() {
        return tcPathTeamContacts;
    }

    public void setTcPathTeamContacts(String tcPathTeamContacts) {
        this.tcPathTeamContacts = tcPathTeamContacts;
    }

    public String getTcPathPersonContacts() {
        return tcPathPersonContacts;
    }

    public void setTcPathPersonContacts(String tcPathPersonContacts) {
        this.tcPathPersonContacts = tcPathPersonContacts;
    }

    public String getTcBaseContext(){
        return protocol + "://" + host + ":" + port;
    }

    public String getTcCirclesURI() {
        return protocol + "://" + host + ":" + port + tcPathCircles;
    }

    public String getTcTeamsURI() {
        return protocol + "://" + host + ":" + port + tcPathTeams;
    }

    public String getTcLocalCircleURI() {
        return protocol + "://" + host + ":" + port + tcPathLocalCircle;
    }

    public String getTcContactsURI() {
        return protocol + "://" + host + ":" + port + tcPathContacts;
    }

    public String getTcTeamContactsURI() {
        return protocol + "://" + host + ":" + port + tcPathTeamContacts;
    }

    public String getTcPersonContactsURI() {
        return protocol + "://" + host + ":" + port + tcPathPersonContacts;
    }

}
