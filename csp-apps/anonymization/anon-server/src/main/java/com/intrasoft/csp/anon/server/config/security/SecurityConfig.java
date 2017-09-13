package com.intrasoft.csp.anon.server.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Value("${enable.oam}")
    boolean enableOAM;

    @Override
    protected void configure( HttpSecurity httpSecurity ) throws Exception {

if (enableOAM){
    httpSecurity
            .authorizeRequests()
            .antMatchers( "/" ).permitAll()
            .antMatchers( "/js/**", "/css/**", "/webjars/****" ).permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(new AuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
}

    }
}


