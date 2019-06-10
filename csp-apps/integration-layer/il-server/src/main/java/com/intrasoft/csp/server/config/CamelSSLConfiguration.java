package com.intrasoft.csp.server.config;

import org.apache.camel.CamelContext;
import org.apache.camel.component.http4.HttpComponent;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.apache.camel.util.jsse.*;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Created by iskitsas on 5/17/17.
 */
@Configuration
public class CamelSSLConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(CamelSSLConfiguration.class);
    public static final String INTERNAL = "internal";
    public static final String EXTERNAL = "external";


    @Autowired
    Environment env;

    @Bean
    CamelContextConfiguration contextConfiguration() {
        return new CamelContextConfiguration() {
            @Override
            public void beforeApplicationStart(CamelContext camelContext) {

            }

            @Override
            public void afterApplicationStart(CamelContext camelContext) {
                setHttp4Component(INTERNAL, camelContext);
                setHttp4Component(EXTERNAL, camelContext);
                LOG.info("Camel HTTP4 configuration complete");
            }
        };
    }

    HttpComponent setHttp4Component(String sslType, CamelContext camelContext) {
        HttpComponent httpComponent = new HttpComponent();
        Boolean useSsl = Boolean.valueOf(env.getProperty(sslType+".use.ssl"));
        String protocol = env.getProperty(sslType + ".ssl.endpoint.protocol");
        LOG.debug("UseSSL = {}, Protocol = {}", useSsl, protocol);
        if(useSsl) {
            String resource = env.getProperty(sslType + ".ssl.keystore.resource"); //jks
            String passphrase = env.getProperty(sslType + ".ssl.keystore.passphrase");

            KeyStoreParameters ksp = new KeyStoreParameters();
            ksp.setResource(resource);
            ksp.setPassword(passphrase);

            KeyManagersParameters kmp = new KeyManagersParameters();
            kmp.setKeyStore(ksp);
            kmp.setKeyPassword(passphrase);

            TrustManagersParameters tmp = new TrustManagersParameters();
            tmp.setKeyStore(ksp);

            SSLContextParameters scp = new SSLContextParameters();
            scp.setKeyManagers(kmp);
//            scp.setTrustManagers(tmp);

            httpComponent.setSslContextParameters(scp);

        }
        httpComponent.setX509HostnameVerifier(NoopHostnameVerifier.INSTANCE);
        camelContext.addComponent(protocol,httpComponent);
        return httpComponent;
    }

    /*not used*/
    @Deprecated
    void keyStore() throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException {
        InputStream is = new FileInputStream("cacert.crt");
        // You could get a resource as a stream instead.

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate caCert = (X509Certificate)cf.generateCertificate(is);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        //ks.setKeyEntry("",);
        ks.load(null); // You don't need the KeyStore instance to come from a file.
        ks.setCertificateEntry("caCert", caCert);

        tmf.init(ks);
    }
}
