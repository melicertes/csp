package com.fraunhofer.csp.rt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Majid Salehi on 4/8/17.
 */
@SpringBootApplication
public class RtAdapterEmitterApplication {

	public static void main(String[] args) {
		SpringApplication.run(RtAdapterEmitterApplication.class, args);
	}

	@Bean
	public RestTemplate geRestTemplate() {
		return new RestTemplate();
	}

}
