package eu.europa.csp.vcbadmin.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7075787673394688668L;
	private String timezone;

	public CustomUserDetails(String username, String password,
			Collection<? extends GrantedAuthority> authorities, String timezone) {
		super(username, password, authorities);
		this.setTimezone(timezone);
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
}