package com.intrasoft.csp.ccs.server;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CcsServer {

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(CcsServer.class);

    public static void main(String[] args) {

        LOG.info("**** Starting CSP CCS server ****");
        SpringApplication.run(CcsServer.class, args);

    }

}
