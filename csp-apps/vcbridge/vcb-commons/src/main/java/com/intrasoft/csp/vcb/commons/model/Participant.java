package com.intrasoft.csp.vcb.commons.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "vcb_participant")
public class Participant {
	public Participant() {

	}

	public Participant(String email, String username, String firstname, String lastname, String password,
			Meeting meeting) {
		this(email, username, firstname, lastname, password);
		this.meeting = meeting;
	}

	public Participant(String email, String username, String firstname, String lastname, String password) {
		this.email = email;
		this.username = username;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Meeting getMeeting() {
		return meeting;
	}

	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}

	@Id
	@GeneratedValue
	private Long id;
	private String email;
	private String username;
	private String password;
	private String firstname;
	private String lastname;

	@NotNull
	@ManyToOne
	private Meeting meeting;

	@Override
	public String toString() {
		return "Participant [id=" + id + ", email=" + email + ", username=" + username + ", password=" + password + "]";
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

	public String getFullname() {
		return this.firstname + " " + this.lastname;
	}
}