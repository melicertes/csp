package com.intrasoft.csp.vcb.admin.config;

import com.intrasoft.csp.vcb.admin.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
//@EnableJpaRepositories("com.intrasoft.csp.vcb.commons.model.*")
@ComponentScan("com.intrasoft.csp.vcb.admin.config, com.intrasoft.csp.vcb.admin.service")
//@EntityScan("com.intrasoft.csp.vcb.commons.model.*")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    //@Autowired
    private UserDetailsService userDetailsService;

    public WebSecurityConfig(@Qualifier("customUserDetailsService") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    @Value("${enable.oam}")
    boolean enableOAM;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(userDetailsService);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    AbstractUserDetailsAuthenticationProvider abstractUserDetailsAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthProvider = new DaoAuthenticationProvider();
        daoAuthProvider.setPasswordEncoder(passwordEncoder);
        daoAuthProvider.setUserDetailsService(userDetailsService);
        return daoAuthProvider;
    }

    @Autowired
    @Override
    public void configure(AuthenticationManagerBuilder myAuthenticationManager) throws Exception {
        myAuthenticationManager.authenticationProvider(customAuthenticationProvider());
        myAuthenticationManager.authenticationProvider(abstractUserDetailsAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.headers().frameOptions().disable();
        if (enableOAM) {
            http.addFilterBefore(new AuthorizationFilter(authenticationManager()), BasicAuthenticationFilter.class);
            http.authenticationProvider(customAuthenticationProvider());
        } else {
            http.authorizeRequests().antMatchers("/resources/**").permitAll().antMatchers("/register").permitAll()
                    .antMatchers("/console/**").permitAll()
                    .anyRequest().authenticated().and().formLogin().loginPage("/login").usernameParameter("username")
                    .permitAll().and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll();
        }
    }
}