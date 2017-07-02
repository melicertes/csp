package eu.europa.csp.vcbadmin.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7075787673394688668L;
	private String timezone;
	private String firstname;
	private String lastname;

	public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities,
			String timezone, String firstname, String lastname) {
		super(username, password, authorities);
		this.setTimezone(timezone);
		this.setFirstname(firstname);
		this.setLastname(lastname);
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

}