package eu.europa.csp.vcbadmin.config;

import eu.europa.csp.vcbadmin.model.CustomUserDetails;
import eu.europa.csp.vcbadmin.model.User;
import eu.europa.csp.vcbadmin.service.UserDetailsService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class CustomAuthenticationProvider implements AuthenticationProvider {

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
            System.out.println(demoAuthentication);
            return demoAuthentication;
        } else {
            System.out.println("error creating user..");
            return null;
        }

	}

	@Override
	public boolean supports(Class<?> authentication) {
		System.out.println(authentication);
        return UserPassAuthenticationToken.class.isAssignableFrom(authentication);
    }

}