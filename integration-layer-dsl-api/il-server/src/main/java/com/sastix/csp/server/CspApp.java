package com.sastix.csp.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CspApp {
	private static final Logger LOG = (Logger) LoggerFactory.getLogger(CspApp.class);
	public static void main(String[] args) {
		LOG.info("**** Starting CSP server ****");
		SpringApplication.run(CspApp.class, args);
	}
}
