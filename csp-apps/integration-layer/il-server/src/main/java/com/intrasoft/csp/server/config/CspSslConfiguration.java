package com.intrasoft.csp.server.config;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;

/**
 * Created by iskitsas on 5/18/17.
 */
@Configuration
public class CspSslConfiguration {
    public static final String INTERNAL = "internal";
    public static final String EXTERNAL = "external";

    @Value("${internal.use.ssl}")
    Boolean internalUseSSL;

    @Value("${internal.ssl.endpoint.protocol}")
    String internalSslEndpointProtocol;

    @Value("${internal.ssl.keystore.resource}")
    String internalSslKeystoreResource;

    @Value("${internal.ssl.keystore.passphrase}")
    String internalSslKeystorePassphrase;

    @Value("${external.use.ssl}")
    Boolean externalUseSSL;

    @Value("${external.ssl.endpoint.protocol}")
    String externalSslEndpointProtocol;

    @Value("${external.ssl.keystore.resource}")
    String externalSslKeystoreResource;

    @Value("${external.ssl.keystore.passphrase}")
    String externalSslKeystorePassphrase;

    @Value("${external.ssl.port}")
    String externalSslPort;

    @Value("${server.ssl.allow.all.hostname}")
    Boolean serverSslAllowAllHostname;

    @Value("${server.ssl.client-auth}")
    String serverSslClientAuth;

    @Value("${server.ssl.key-password}")
    String serverSslKeyPassword;

    @Value("${server.ssl.key-store-password}")
    String serverSslKeyStorePassword;

    @Value("${server.ssl.key-store}")
    String serverSslKeyStore;

    @Value("${server.ssl.enabled}")
    Boolean serverSslEnabled;


    public Boolean getInternalUseSSL() {
        return internalUseSSL;
    }

    @PostConstruct
    public void init(){
        if(serverSslAllowAllHostname!=null && serverSslAllowAllHostname) {
            //allowing 'localhost'
            HttpsURLConnection.setDefaultHostnameVerifier(NoopHostnameVerifier.INSTANCE);
        }
    }

    public void setInternalUseSSL(Boolean internalUseSSL) {
        this.internalUseSSL = internalUseSSL;
    }

    public String getInternalSslEndpointProtocol() {
        return internalSslEndpointProtocol;
    }

    public void setInternalSslEndpointProtocol(String internalSslEndpointProtocol) {
        this.internalSslEndpointProtocol = internalSslEndpointProtocol;
    }

    public String getInternalSslKeystoreResource() {
        return internalSslKeystoreResource;
    }

    public void setInternalSslKeystoreResource(String internalSslKeystoreResource) {
        this.internalSslKeystoreResource = internalSslKeystoreResource;
    }

    public String getInternalSslKeystorePassphrase() {
        return internalSslKeystorePassphrase;
    }

    public void setInternalSslKeystorePassphrase(String internalSslKeystorePassphrase) {
        this.internalSslKeystorePassphrase = internalSslKeystorePassphrase;
    }

    public Boolean getExternalUseSSL() {
        return externalUseSSL;
    }

    public void setExternalUseSSL(Boolean externalUseSSL) {
        this.externalUseSSL = externalUseSSL;
    }

    public String getExternalSslEndpointProtocol() {
        return externalSslEndpointProtocol;
    }

    public void setExternalSslEndpointProtocol(String externalSslEndpointProtocol) {
        this.externalSslEndpointProtocol = externalSslEndpointProtocol;
    }

    public String getExternalSslKeystoreResource() {
        return externalSslKeystoreResource;
    }

    public void setExternalSslKeystoreResource(String externalSslKeystoreResource) {
        this.externalSslKeystoreResource = externalSslKeystoreResource;
    }

    public String getExternalSslKeystorePassphrase() {
        return externalSslKeystorePassphrase;
    }

    public void setExternalSslKeystorePassphrase(String externalSslKeystorePassphrase) {
        this.externalSslKeystorePassphrase = externalSslKeystorePassphrase;
    }

    public String getExternalSslPort() {
        return externalSslPort;
    }

    public void setExternalSslPort(String externalSslPort) {
        this.externalSslPort = externalSslPort;
    }

    public Boolean getServerSslAllowAllHostname() {
        return serverSslAllowAllHostname;
    }

    public void setServerSslAllowAllHostname(Boolean serverSslAllowAllHostname) {
        this.serverSslAllowAllHostname = serverSslAllowAllHostname;
    }

    public String getServerSslClientAuth() {
        return serverSslClientAuth;
    }

    public void setServerSslClientAuth(String serverSslClientAuth) {
        this.serverSslClientAuth = serverSslClientAuth;
    }

    public String getServerSslKeyPassword() {
        return serverSslKeyPassword;
    }

    public void setServerSslKeyPassword(String serverSslKeyPassword) {
        this.serverSslKeyPassword = serverSslKeyPassword;
    }

    public String getServerSslKeyStorePassword() {
        return serverSslKeyStorePassword;
    }

    public void setServerSslKeyStorePassword(String serverSslKeyStorePassword) {
        this.serverSslKeyStorePassword = serverSslKeyStorePassword;
    }

    public String getServerSslKeyStore() {
        return serverSslKeyStore;
    }

    public void setServerSslKeyStore(String serverSslKeyStore) {
        this.serverSslKeyStore = serverSslKeyStore;
    }

    public Boolean getServerSslEnabled() {
        return serverSslEnabled;
    }

    public void setServerSslEnabled(Boolean serverSslEnabled) {
        this.serverSslEnabled = serverSslEnabled;
    }
}
