package com.intrasoft.csp.anon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AnonApp {

	public static void main(String[] args) {
		SpringApplication.run(AnonApp.class, args);
	}
}
