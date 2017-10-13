package com.intrasoft.csp.misp.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AdapterApp {

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(AdapterApp.class);

    public static void main(String[] args) {
        LOG.info("**** Starting MISP ADAPTER app ****");
        SpringApplication.run(AdapterApp.class, args);
    }
}
