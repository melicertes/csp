package com.intrasoft.csp.ccs;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableWebMvc
//@EnableTransactionManagement
//@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
public class CspCcs {

    public static void main(String[] args) {
        SpringApplication.run(CspCcs.class, args);
    }

}
