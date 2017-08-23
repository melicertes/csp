package com.intrasoft.csp.server.policy;

import com.intrasoft.csp.server.policy.service.SharingPolicyService;
import com.intrasoft.csp.server.policy.service.impl.SharingPolicyImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.intrasoft.csp.anon")
public class SharingPolicyConfig {

    @Bean
    SharingPolicyService getSharingPolicyService(){
        return new SharingPolicyImpl();
    }
}
