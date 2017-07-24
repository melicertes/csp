package com.intrasoft.csp.anon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AnonApp {
    private static final Logger LOG = (Logger) LoggerFactory.getLogger(AnonApp.class);
	public static void main(String[] args) {
        LOG.info("**** Starting ANONYMIZATION app ****");
		SpringApplication.run(AnonApp.class, args);
	}
}
