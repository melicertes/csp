package com.intrasoft.csp.server.config;

import com.intrasoft.csp.commons.validators.IntegrationDataValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

/**
 * Created by iskitsas on 6/13/17.
 */
@Configuration
public class CspValidatorsConfiguration {
    @Autowired
    SpringValidatorAdapter springValidatorAdapter;

    @Bean
    IntegrationDataValidator integrationDataValidator(){
        return new IntegrationDataValidator(springValidatorAdapter);
    }
}
