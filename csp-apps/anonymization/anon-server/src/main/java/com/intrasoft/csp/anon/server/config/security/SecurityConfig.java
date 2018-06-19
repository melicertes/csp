package com.intrasoft.csp.anon.server.config.security;

import com.intrasoft.csp.libraries.headersauth.AuthorizationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Value("${enable.oam}")
    boolean enableOAM;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        if (enableOAM) {
            http.addFilterBefore(new AuthorizationFilter(), BasicAuthenticationFilter.class).antMatcher("/**");
        }
    }
}


