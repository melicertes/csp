package com.intrasoft.csp.conf.clientcspapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class ConfClientCspApplication {
	public static void main(String[] args) {
		SpringApplication.run(ConfClientCspApplication.class, args);
	}
}
