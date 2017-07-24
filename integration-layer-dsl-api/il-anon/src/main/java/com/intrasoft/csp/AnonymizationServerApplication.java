package com.intrasoft.csp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AnonymizationServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnonymizationServerApplication.class, args);
	}
}
