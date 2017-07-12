package com.instrasoft.csp.ccs;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
//@EnableWebMvc
//@EnableTransactionManagement
//@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
public class CspCcs {

    public static void main(String[] args) {
        SpringApplication.run(CspCcs.class, args);
    }

}
