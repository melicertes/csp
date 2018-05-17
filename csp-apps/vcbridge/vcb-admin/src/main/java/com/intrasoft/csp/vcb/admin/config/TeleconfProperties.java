package com.intrasoft.csp.vcb.admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("jitsi")
public class TeleconfProperties {

    @Value("${teleconf.protocol}")
    private String protocol;

    @Value("${teleconf.host}")
    private String host;

    @Value("${teleconf.port}")
    private Integer port;

    @Value("${teleconf.path}")
    private String path;


    public String buildURI() {
        return protocol + "://" + host + ":" + port + (path.equals("") ? "" : path + "/");
    }

}
