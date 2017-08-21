package com.intrasoft.csp.conf.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
public class ConfApp {

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(ConfApp.class);

    public static void main(String[] args) {
        LOG.info("**** Starting CSP Configuration Server ****");
        SpringApplication.run(ConfApp.class, args);
    }

}
