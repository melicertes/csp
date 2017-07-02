package eu.europa.csp.vcbadmin.service;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import eu.europa.csp.vcbadmin.model.CustomUserDetails;
import eu.europa.csp.vcbadmin.model.User;
import eu.europa.csp.vcbadmin.repository.UserRepository;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	private final Logger log = LoggerFactory.getLogger(UserDetailsService.class);

	private UserRepository userRepository;

	@Autowired
	public UserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(final String login) {

		log.debug("Authenticating {}", login);
		User user = userRepository.findByEmail(login.toLowerCase()).orElseThrow(
				() -> new UsernameNotFoundException(String.format("User with email=%s was not found", login)));

		Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getRole().name());
		grantedAuthorities.add(grantedAuthority);
		return new CustomUserDetails(user.getEmail(), user.getPassword(), grantedAuthorities, user.getTimezone(),
				user.getFirstName(), user.getLastName());
		// return new
		// org.springframework.security.core.userdetails.User(user.getEmail(),
		// user.getPassword(), grantedAuthorities);

	}
}
