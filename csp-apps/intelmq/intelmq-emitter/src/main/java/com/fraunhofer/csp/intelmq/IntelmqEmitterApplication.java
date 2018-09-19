package com.fraunhofer.csp.intelmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Majid Salehi on 20/04/2018
 */
@SpringBootApplication
public class IntelmqEmitterApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntelmqEmitterApplication.class, args);
	}

	@Bean
	public RestTemplate geRestTemplate() {
		return new RestTemplate();
	}

}
