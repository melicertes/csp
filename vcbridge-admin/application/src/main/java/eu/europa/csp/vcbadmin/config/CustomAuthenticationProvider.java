package eu.europa.csp.vcbadmin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import eu.europa.csp.vcbadmin.model.CustomUserDetails;
import eu.europa.csp.vcbadmin.model.User;
import eu.europa.csp.vcbadmin.service.UserDetailsService;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	// This would be a JPA repository to snag your user entities
	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		UsernamePasswordAuthenticationToken demoAuthentication = (UsernamePasswordAuthenticationToken) authentication;
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
			System.out.println("FUCK");
			System.out.println("FUCK");
			System.out.println("FUCK");
			System.out.println("FUCK");
			System.out.println("FUCK");
			System.out.println("FUCK");
			System.out.println("FUCK");
			System.out.println("FUCK");
			System.out.println("FUCK");
			System.out.println("FUCK");
			System.out.println("FUCK");
			System.out.println("FUCK");
			return null;
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println("EDW");
		System.out.println(authentication);
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

}