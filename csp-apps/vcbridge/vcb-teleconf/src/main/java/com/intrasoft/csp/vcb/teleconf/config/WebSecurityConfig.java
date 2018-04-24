package com.intrasoft.csp.vcb.teleconf.config;

import com.intrasoft.csp.vcb.teleconf.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@ComponentScan("com.intrasoft.csp.vcb.teleconf.config, com.intrasoft.csp.vcb.teleconf.service")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

        http
                //Start chain for restricting access.
                .authorizeRequests()
                //The following paths...
                .antMatchers("/webapp/**", "/console/**", "/login**", "/app/**")
                // ...are accessible to all users (authenticated or not).
                .permitAll();

        http
                .exceptionHandling()
                .authenticationEntryPoint(new ContinueEntryPoint("/login"))
                .and()
                //Start chain for restricting access.
                .authorizeRequests()
                //All remaining paths...
                .anyRequest()
                // ...require user to at least be authenticated
                .authenticated()
                .and()
                // And if a user needs to be authenticated...
                .formLogin()
                // ...redirect them to /login
                .loginPage("/login")
                .permitAll()
                .and()
                // If user isn't authorised to access a path...
                .exceptionHandling()
                // ...redirect them to /403 or to custom error handling class
                .accessDeniedPage("/error.html")
                .and()
                // logout reguests
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessHandler(new CustomLogoutSuccessHandler()).invalidateHttpSession(true);


        http.headers().frameOptions().disable();

        http.csrf().disable();
    }


    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("*.css");
        web.ignoring().antMatchers("*.js");
    }


    @Bean
    public AuthenticationSuccessHandler successHandler() {
        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
        handler.setUseReferer(true);
        return handler;
    }

}
