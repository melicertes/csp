package eu.europa.csp.vcbadmin.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserPassAuthenticationToken extends UsernamePasswordAuthenticationToken {
    public UserPassAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public UserPassAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
