package com.intrasoft.csp.regrep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class RegularReportsApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(RegularReportsApplication.class);
    }

}
