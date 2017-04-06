package com.sastix.csp.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class IntegrationServer {

    public static void main(String[] args) {
        SpringApplication.run(IntegrationServer.class, args);
    }
}
