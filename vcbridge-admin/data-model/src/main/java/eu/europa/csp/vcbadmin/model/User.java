package eu.europa.csp.vcbadmin.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
//import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotNull;

import org.hibernate.engine.internal.Cascade;

//import com.fasterxml.jackson.annotation.JsonIgnore;

import eu.europa.csp.vcbadmin.constants.UserRole;

@Entity
@Table(name = "vcb_user")
public class User {

	@Id
	@GeneratedValue
	private Long id;

	// @NotNull
	@Column(name = "first_name")
	private String firstName;

	// @NotNull
	@Column(name = "last_name")
	private String lastName;

	@NotNull
	@Column(name = "email", unique = true)
	private String email;

	@NotNull
	// @JsonIgnore
	@Column(name = "password")
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(name = "role")
	private UserRole role = UserRole.USER;

	@Transient
	private String passwordConfirm;

	public User() {
	}
	
	public EmailTemplate getInvitation() {
		return invitation;
	}

	public void setInvitation(EmailTemplate invitation) {
		this.invitation = invitation;
	}

	public EmailTemplate getCancellation() {
		return cancellation;
	}

	public void setCancellation(EmailTemplate cancellation) {
		this.cancellation = cancellation;
	}

	@OneToOne
	private EmailTemplate invitation;
	
	@OneToOne
	private EmailTemplate cancellation;

	@OneToMany(mappedBy = "user")
	private List<Meeting> meetings;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}

	public List<Meeting> getMeetings() {
		return meetings;
	}

	public void setMeetings(List<Meeting> meetings) {
		this.meetings = meetings;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", password=" + password + ", role=" + role + "]";
	}
}
