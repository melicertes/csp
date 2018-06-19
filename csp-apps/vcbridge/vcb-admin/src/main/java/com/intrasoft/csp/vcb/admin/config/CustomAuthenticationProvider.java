package com.intrasoft.csp.vcb.admin.config;

import com.intrasoft.csp.vcb.admin.service.UserDetailsService;
import com.intrasoft.csp.vcb.admin.model.CustomUserDetails;

import com.intrasoft.csp.vcb.commons.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class CustomAuthenticationProvider implements AuthenticationProvider {
    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    // This would be a JPA repository to snag your user entities
    private UserDetailsService userDetailsService;

    public CustomAuthenticationProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {


        UsernamePasswordAuthenticationToken demoAuthentication = (UserPassAuthenticationToken) authentication;
        User u = userDetailsService.createUserIfNotExists(demoAuthentication.getName());
        if (u != null) {
            // profile automatically created or exists
            CustomUserDetails dbentity = (CustomUserDetails) demoAuthentication.getPrincipal();
            dbentity.setFirstname(u.getFirstName());
            dbentity.setLastname(u.getLastName());
            dbentity.setTimezone(u.getTimezone());
            return demoAuthentication;
        } else {
            log.error("Error creating user..");
            return null;
        }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UserPassAuthenticationToken.class.isAssignableFrom(authentication);
    }

}