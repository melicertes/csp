package com.intrasoft.csp.conf.clientcspapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConfClientCspApplication {

	private static final Logger LOG = (Logger) LoggerFactory.getLogger(ConfClientCspApplication.class);

	public static void main(String[] args) {
		LOG.info("**** Starting CSP Configuration Client Application ****");
		SpringApplication.run(ConfClientCspApplication.class, args);
	}
}
