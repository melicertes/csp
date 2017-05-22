package com.sastix.csp.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

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

    public Boolean getInternalUseSSL() {
        return internalUseSSL;
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
}
